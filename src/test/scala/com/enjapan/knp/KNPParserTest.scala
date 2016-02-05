package com.enjapan.knp

import cats.data.Xor
import com.enjapan.knp.models.{Argument, Predicate}
import org.scalatest.{FunSuite, Matchers}

/**
  * Created by Ugo Bataillard on 2/2/16.
  */
class KNPParserTest extends FunSuite with Matchers {

  test("testParse") {
    val knpOutput =
      """# S-ID:123 KNP:4.2-ffabecc DATE:2015/04/10 SCORE:-18.02647
         |* 1D <BGH:解析/かいせき><文頭><サ変><助詞><連体修飾><体言>
         |+ 1D <BGH:構文/こうぶん><文節内><係:文節内><文頭><体言>
         |構文 こうぶん 構文 名詞 6 普通名詞 1 * 0 * 0 "代表表記:構文/こうぶん カテゴリ:抽象物" <代表表記:構文/こうぶん>
         |+ 2D <BGH:解析/かいせき><助詞><連体修飾><体言>
         |解析 かいせき 解析 名詞 6 サ変名詞 2 * 0 * 0 "代表表記:解析/かいせき カテゴリ:抽象物 ドメイン:教育・学習;科学・技術" <代表表記:解析/かいせき>
         |の の の 助詞 9 接続助詞 3 * 0 * 0 NIL <かな漢字><ひらがな><付属>
         |* 2D <BGH:実例/じつれい><ヲ><助詞><体言><係:ヲ格>
         |+ 3D <BGH:実例/じつれい><ヲ><助詞><体言><係:ヲ格>
         |実例 じつれい 実例 名詞 6 普通名詞 1 * 0 * 0 "代表表記:実例/じつれい カテゴリ:抽象物" <代表表記:実例/じつれい>
         |を を を 助詞 9 格助詞 1 * 0 * 0 NIL <かな漢字><ひらがな><付属>
         |* -1D <BGH:示す/しめす><文末><句点><用言:動>
         |+ -1D <BGH:示す/しめす><文末><句点><用言:動>
         |示す しめす 示す 動詞 2 * 0 子音動詞サ行 5 基本形 2 "代表表記:示す/しめす" <代表表記:示す/しめす><正規化代表表記:示す/しめす>
         |。 。 。 特殊 1 句点 1 * 0 * 0 NIL <英記号><記号><文末><付属>
         |EOS""".stripMargin

    val parser = new KNPParser()

    val res = parser.parse(knpOutput.split("\n"))
    res.isRight shouldBe true
    val blist = res.getOrElse(throw new Exception("Should not happen"))

    val bunsetsuList = blist.bunsetsuList
    val tagList = blist.tagList
    val morphemeList = blist.morphemeList

    bunsetsuList should have size 3
    tagList should have size 4
    morphemeList should have size 7

    morphemeList.map(_.midasi).mkString("") shouldBe "構文解析の実例を示す。"
    blist.sid shouldBe "123"

    bunsetsuList(0).paType shouldBe Predicate
    bunsetsuList(1).paType shouldBe Predicate
    bunsetsuList(2).paType shouldBe Argument("動")

    // Check parent / children relations
    bunsetsuList(1).parentId shouldBe 2
    bunsetsuList(1).parent shouldBe Some(bunsetsuList(2))
    bunsetsuList(2).parentId shouldBe -1
    bunsetsuList(2).parent shouldBe None
    bunsetsuList(1).children should contain theSameElementsAs Seq(bunsetsuList(0))
    bunsetsuList(0).children should be ('empty)

    tagList(1).parent.get shouldBe tagList(2)
    tagList(2).children should contain theSameElementsAs Seq(tagList(1))
  }

  test("testParseBunsetsu") {
    val bunsetsuString =
      """* -1D <BGH:解析/かいせき><文頭><文末><サ変><体言><用言:判><体言止><レベル:C>
        |+ 1D <BGH:構文/こうぶん><文節内><係:文節内><文頭><体言><名詞項候補><先行詞候補><正規化代表表記:構文/こうぶん>
        |構文 こうぶん 構文 名詞 6 普通名詞 1 * 0 * 0 "代表表記:構文/こうぶん カテゴリ:抽象物" <代表表記:構文/こうぶん>
        |+ -1D <BGH:解析/かいせき><文末><体言><用言:判><体言止><レベル:C>
        |解析 かいせき 解析 名詞 6 サ変名詞 2 * 0 * 0 "代表表記:解析/かいせき カテゴリ:抽象物 ドメイン:教育・学習;科学・技術" <代表表記:解析/かいせき>""".stripMargin

    val parser = new KNPParser()

    val res = parser.parseBunsetsu(bunsetsuString.split("\n"))

    res shouldBe a [Xor.Right[_]]
    val bnst = res.getOrElse(throw new Exception("Should not happen"))

    bnst.parentId shouldBe -1
    bnst.dpndtype shouldBe "D"
    bnst.paType shouldBe Argument("判")
    bnst.repName shouldBe None
    bnst.features should have size 8
    bnst.tags should have size 2
    bnst.tags.map(_.dpndtype) should contain only "D"
    bnst.tags.flatMap(_.morphemes) should have size 2
  }

  test("testParseBunsetsuInvalid") {
    val bunsetsuString =
      """* -1D <BGH:解析/かいせき><文頭><文末><サ変><体言><用言:判><体言止><レベル:C>
        |構文 こうぶん 構文 名詞 6 普通名詞 1 * 0 * 0 "代表表記:構文/こうぶん カテゴリ:抽象物" <代表表記:構文/こうぶん>
        |+ 1D <BGH:構文/こうぶん><文節内><係:文節内><文頭><体言><名詞項候補><先行詞候補><正規化代表表記:構文/こうぶん>
        |+ -1D <BGH:解析/かいせき><文末><体言><用言:判><体言止><レベル:C>
        |解析 かいせき 解析 名詞 6 サ変名詞 2 * 0 * 0 "代表表記:解析/かいせき カテゴリ:抽象物 ドメイン:教育・学習;科学・技術" <代表表記:解析/かいせき>"""
        .stripMargin.split("\n")
    val parser = new KNPParser()
    val res = parser.parseBunsetsu(bunsetsuString)
    res shouldBe a [Xor.Left[_]]
  }

  test("testParseTag") {
    val tagStr =
      """+ 1D <BGH:構文/こうぶん><文節内><係:文節内><文頭><体言><名詞項候補><先行詞候補><正規化代表表記:構文/こうぶん>
        |構文 こうぶん 構文 名詞 6 普通名詞 1 * 0 * 0 "代表表記:構文/こうぶん カテゴリ:抽象物" <代表表記:構文/こうぶん>
        |解析 かいせき 解析 名詞 6 サ変名詞 2 * 0 * 0 "代表表記:解析/かいせき カテゴリ:抽象物 ドメイン:教育・学習;科学・技術" <代表表記:解析/かいせき> """
        .stripMargin.split("\n")

    val parser = new KNPParser()
    val res = parser.parseTag(tagStr)
    res shouldBe a [Xor.Right[_]]
    val tag = res.getOrElse(throw new Exception("Should not happen"))

    tag.dpndtype shouldBe "D"
    tag.parentId shouldBe 1
    tag.morphemes should have size 2
    tag.surface shouldBe "構文解析"
    tag.paType shouldBe Predicate
    tag.repName shouldBe Some("構文/こうぶん")
  }

  test("testParseTagInvalid") {
    val tagStr =
      """+ 1- <BGH:構文/こうぶん><文節内><係:文節内><文頭><体言><名詞項候補><先行詞候補><正規化代表表記:構文/こうぶん>
        |構文 こうぶん 構文 名詞 6 普通名詞 1 * 0 * 0 "代表表記:構文/こうぶん カテゴリ:抽象物" <代表表記:構文/こうぶん>
        |解析 かいせき 解析 名詞 6 サ変名詞 2 * 0 * 0 "代表表記:解析/かいせき カテゴリ:抽象物 ドメイン:教育・学習;科学・技術" <代表表記:解析/かいせき> """
        .stripMargin.split("\n")

    val parser = new KNPParser()
    val res = parser.parseTag(tagStr)
    res shouldBe a [Xor.Left[_]]
  }

  test("parseFeatures") {
    val tagStr = "<BGH:構文/こうぶん><文節内><係:文節内><文頭><体言><名詞項候補><先行詞候補><正規化代表表記:構文/こうぶん>"
    val parser = new KNPParser()
    val (f,_) = parser.parseFeatures(tagStr)
    f("BGH") shouldBe "構文/こうぶん"
    f("係") shouldBe "文節内"
    f.get("先行詞候補") should be ('defined)
    f.get("dummy") shouldBe None
    f("正規化代表表記") shouldBe "構文/こうぶん"
  }

  test("testParsePAS") {

    val pasStr = "分/ふん:判1:ガ/U/-/-/-/-;ヲ/U/-/-/-/-;ニ/U/-/-/-/-;デ/C/車/1/0/14;カラ/U/-/-/-/-;ヨリ/C/インター/0/0/14;マデ/U/-/-/-/-;ヘ/U/-/-/-/-;時間/U/-/-/-/-"
    val parser = new KNPParser()
    val (Some(pas)) = parser.parsePAS(pasStr)

    pas.cfid shouldBe "分/ふん:判1"
    val args = pas.arguments
    args should have size 2
    args("デ").relationName shouldBe "デ"
    args("デ").relationType shouldBe "C"
    args("デ").argId shouldBe 1
    args("デ").argWord shouldBe "車"
    args("デ").argSentId shouldBe "14"
    args.get("ガ") shouldBe None
  }

  test("testParseRels") {

    val tagStr =
      """<rel type="時間" target="一九九五年" sid="950101003-002" id="1"/>
        |<rel type="ヲ" target="衆院" sid="950101003-002" id="3"/>
        |<rel type="ガ" target="不特定:人1"/>
        |<rel type="時間" target="国会前" sid="950101003-asd" id="16"/>
      """.stripMargin.replace("\n","")

    val parser = new KNPParser()
    val (_, rels) = parser.parseFeatures(tagStr)
    rels should have size 3
    val rel = rels.head

    rel.id shouldBe Some(1)
    rel.mode shouldBe None
    rel.`type` shouldBe "時間"
    rel.sid shouldBe "950101003-002"
    rel.target shouldBe "一九九五年"
  }

}
