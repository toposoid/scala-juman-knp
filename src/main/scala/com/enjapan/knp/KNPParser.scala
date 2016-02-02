package com.enjapan.knp

/**
  * Created by Ugo Bataillard on 2/2/16.
  */
import com.enjapan.juman.JumanParser
import com.enjapan.knp.models._

import scala.annotation.tailrec
import scala.collection.mutable
import scala.util.matching.Regex

/**
  * Created by Ugo Bataillard on 2/1/16.
  */
object KNPParser {
  val TAG: String = classOf[KNPParser].getSimpleName
  val SID_REGEX = """# S-ID:([^\s]*).*$""".r
  val BUNSETSU_REGEX = """\* (-?\d+)([DPIA])(.*)$""".r
  val BUNSETSU_REP_NAME_REGEX = """<正規化代表表記:([^\"\s]+?)>""".r
  val TAG_REGEX = """\+ (-?\d+)(\w)(.*)$""".r
  val REL_REGEX = """rel type=\"([^\s]+?)\"(?: mode=\"([^>]+?)\")? target=\"([^\s]+?)\"(?: sid=\"(.+?)\" id=\"(.+?)\")?/""".r
  val WRITER_READER_LIST = Set("著者", "読者")
  val WRITER_READER_CONV_LIST = Map("一人称" -> "著者", "二人称" -> "読者")
}

class KNPParser(val breakingPattern: Regex = "^EOS$".r ) {

  def parse(lines: Iterable[String]): BList = {

    val relevantLines = lines.map(_.trim)
      .filter(_.nonEmpty)
      .takeWhile(!breakingPattern.pattern.matcher(_).find)
      .filterNot(_.startsWith("EOS"))

    relevantLines.find(_.startsWith(";;")) foreach { errorLine =>
      throw new RuntimeException(s"Error found line starting with ';;: $errorLine")
    }

    // Check if there can be only one comment
    val (comment, sid) = relevantLines.find(_.startsWith("#")).map { line =>
      val KNPParser.SID_REGEX(sid) = line
      (line, sid)
    }.getOrElse(("",""))

    val bunsetsus = parseKNPNodeLines[Bunsetsu]("*", relevantLines.drop(1), parseBunsetsu)

    BList(breakingPattern.toString, comment, sid, bunsetsus)
  }

  def parseBunsetsu(lines: Iterable[String]): Bunsetsu = {
    val tags = parseKNPNodeLines[Tag]("+", lines.drop(1), parseTag)
    parseLine(lines.head, KNPParser.BUNSETSU_REGEX).map { case (parentId, dpndtype, fstring) =>
      val repName = KNPParser.BUNSETSU_REP_NAME_REGEX.findFirstMatchIn(fstring) map (_.group(1))
      Bunsetsu(parentId, dpndtype, fstring, repName, tags)
    }.getOrElse{
      throw new RuntimeException(s"Illegal bunsetsu spec: $lines")
    }
  }

  def parseTag(lines: Iterable[String]): Tag = {
    val morphemes = lines.drop(1) map JumanParser.parseMorpheme
    parseLine(lines.head, KNPParser.TAG_REGEX).map { case (parentId, dpndtype, fstring) =>
      val (features, rels, pas) = parseFeatures(fstring, ignoreFirstCharacter = false)
      Tag(parentId, dpndtype, fstring, morphemes.toList, features, rels, pas)
    }.getOrElse {
      throw new RuntimeException(s"Illegal tag spec: $lines")
    }
  }

  def parseKNPNodeLines[T](
    prefix:String,
    lines:Iterable[String], parseNode: Iterable[String] => T): IndexedSeq[T] = {
    @tailrec
    def recParse(lines: Iterable[String], result: List[T]): List[T] = lines match {
      case ls if ls.isEmpty => result
      case ls if ls.head.startsWith(prefix) =>
        val (nodeLines, remainingLines) = lines.tail.span(!_.startsWith(prefix))
        val node = parseNode(Iterable(ls.head) ++ nodeLines)
        recParse(remainingLines, node :: result)
    }
    recParse(lines, List.empty).reverse.toIndexedSeq
  }

  def parseFeatures(fstring: String, ignoreFirstCharacter: Boolean): (Map[String,String], List[Rel], Option[Pas]) = {

    val spec: String = fstring.replaceAll("\\s+$", "")

    spec.split("><").foldLeft((Map.empty[String, String], List.empty[Rel], Option.empty[Pas])) {
      case ((features, rels, pas), feature) =>
        if (feature.startsWith("rel")) {
          (features, rels ++ parseRel(feature), pas)
        }
        else {
          val (key, value) = feature.span(_ != ':')
          (features + (key -> value), rels, if (key == "格解析結果") parsePAS(value) else pas)
        }
    }
  }

  def parsePAS(value: String): Option[Pas] = {
    val cs = value.split(":")
    if (cs.length < 3) {
      None
    }
    else {
      val cfid: String = cs(0) + cs(1)
      val arguments = mutable.Map[String, Argument]()
      for {
        k <- cs.drop(2).mkString("").split(";")
        items = k.split("/") if !(items(1) == "U") && !(items(1) == "-")
      } { arguments.put(items(0), Argument(items(0), items(1), items(2), items(3).toInt, items(5))) }
      Some(Pas(cfid, arguments.toMap))
    }
  }

  def parseRel(fstring:String , consider_writer_reader:Boolean = false): Option[Rel] = {

    KNPParser.REL_REGEX
      .findAllMatchIn(fstring)
      .collectFirst {
        case m if m.subgroups.size >= 4 && m.subgroups(1) != "？" => m.subgroups match {
          case atype :: mode :: target :: sid :: rest =>
            Rel(atype, target, sid, mode, rest.headOption.map(_.toInt))
        }

        case m if consider_writer_reader && m.subgroups.size >= 3 &&
          m.subgroups(1) != "？" &&
          (consider_writer_reader && {
            val target = m.subgroups(2)
            target != "なし" && KNPParser.WRITER_READER_LIST.contains(target) || KNPParser.WRITER_READER_CONV_LIST.contains(target)
          }) => m.subgroups match {
          case atype :: mode :: target :: rest =>
            val t = if (KNPParser.WRITER_READER_LIST.contains(target)) target else KNPParser.WRITER_READER_CONV_LIST(target)
            Rel(atype, t, "", mode, None)
        }
      }
  }

  def parseLine(line: String, linePattern: Regex): Option[(Int, String, String)] = {
    val l = line.trim
    if (l.length == 1 ) {
      None
    }
    else {
      linePattern.findFirstMatchIn(l).map { m =>
        val parentId: Int = m.group(1).toInt
        val dpndtype: String = m.group(2)
        val fstring: String = m.group(3).trim
        (parentId, dpndtype, fstring)
      }
    }
  }
}

