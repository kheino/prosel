package io.heino.prosel

class PropertySelectorTest extends GroovyTestCase {
    def ps

    static def parse(String input) {
        return PropertySelector.parse(input)
    }

    void setUp() {
        ps = new PropertySelector()
    }

    void buildComplexSelector() {
        def foo = new PropertySelector()
        foo.add("bar")
        ps.add("foo", foo)

        def baz = new PropertySelector(UniversalSelector.ALL)
        baz.add("qux")
        ps.add("baz", baz)

        def wak = new PropertySelector(UniversalSelector.BLOCK)
        wak.add("paf")
        def zot = new PropertySelector()
        zot.add("pok", new PropertySelector())
        wak.add("zot", zot)
        ps.add("wak", wak)
    }

    void testDefaultSelectorExcludesAll() {
        assert ps.getUniversalSelector() == UniversalSelector.NONE
    }

    void testParseEmptyStringReturnsDefaultSelector() {
        assert ps == parse("")
    }

    void testParseEmptyBlockReturnsDefaultSelector() {
        assert ps == parse("{}")
    }

    void testParseSimpleBlock() {
        ps.add("foo")
        ps.add("bar")
        assert ps == parse("{ foo, bar }")
    }

    void testParseAllowsRootAsList() {
        ps.add("foo")
        ps.add("bar")
        assert ps == parse("foo, bar")
    }

    void testParseComplexSelector() {
        buildComplexSelector()
        assert ps == parse("foo: bar, baz: { *, qux }, wak: { *?, paf, zot: { pok: !* } }")
    }

    void testParseSelectorWithExtraWhitespace() {
        buildComplexSelector()
        assert ps == parse("  foo  : \n bar  , \t baz  :  {  *  , \u000B qux  }  , \f wak  :  {  *  ?  , \r paf  ,  zot  :  {  pok  :  !  *  }  }  ")
    }

    void testParseSelectorWithNoWhitespace() {
        buildComplexSelector()
        assert ps == parse("foo:bar,baz:{*,qux},wak:{*?,paf,zot:{pok:!*}}")
    }
}
