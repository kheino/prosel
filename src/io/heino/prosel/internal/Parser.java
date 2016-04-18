package io.heino.prosel.internal;

import io.heino.prosel.LexerException;
import io.heino.prosel.ParserException;
import io.heino.prosel.PropertySelector;
import io.heino.prosel.UniversalSelector;

public class Parser {
   private Lexer lexer;

   public PropertySelector parse(String selector) throws ParserException {
      try {
         lexer = new Lexer();
         lexer.analyze(selector);
         PropertySelector ps = new PropertySelector(UniversalSelector.NONE);

         if (lexer.hasNext(TokenType.LBRACE))
            parseBlock(ps);
         else
            parseList(ps);

         return ps;
      } catch (LexerException e) {
         throw new ParserException(e);
      }
   }

   private void parseBlock(PropertySelector ps) throws ParserException {
      skip(TokenType.LBRACE);
      parseList(ps);
      skip(TokenType.RBRACE);
   }

   private void parseList(PropertySelector ps) throws ParserException {
      while (true) {
         if (!parseSelector(ps, SelectorConstraint.NONE))
            break;

         if (!lexer.trySkip(TokenType.COMMA))
            break;
      }
   }

   private boolean parseSelector(PropertySelector ps, SelectorConstraint sc) throws ParserException {
      // Excludes
      if (lexer.trySkip(TokenType.EXCLA)) {
         // Exclude all: !*
         if (lexer.trySkip(TokenType.ASTER)) {
            ps.setUniversalSelector(UniversalSelector.NONE);
         }
         // Exclude property: !name
         else {
            ValueToken t = (ValueToken)next(TokenType.IDENT);
            ps.exclude(t.getValue());
         }
      }
      // Universal selectors
      else if (lexer.trySkip(TokenType.ASTER)) {
         // Limit: *N
         if (lexer.hasNext(TokenType.INT)){
            ValueToken t = (ValueToken)next(TokenType.INT);
            // TODO: t.getValue() is NOT clamped to integer range
            ps.setUniversalSelector(
                  new UniversalSelector(Integer.parseInt(t.getValue())));
         }
         // Block: *?
         else if (lexer.trySkip(TokenType.QUEST)) {
            ps.setUniversalSelector(UniversalSelector.BLOCK);
         }
         // All: *
         else {
            ps.setUniversalSelector(UniversalSelector.ALL);
         }
      }
      // Select property: name [ : { ... } ]
      else if (lexer.hasNext(TokenType.IDENT)) {
         ValueToken name = (ValueToken)lexer.next();
         PropertySelector value;

         if (sc != SelectorConstraint.SINGLE && lexer.trySkip(TokenType.COLON)) {
            value = new PropertySelector(UniversalSelector.NONE);

            if (lexer.hasNext(TokenType.LBRACE))
               parseBlock(value);
            else
               parseSelector(value, SelectorConstraint.SINGLE);
         } else {
            value = new PropertySelector(UniversalSelector.ALL);
         }

         ps.add(name.getValue(), value);
      } else {
         return false;
      }

      return true;
   }

   public Token next(TokenType type) throws ParserException {
      if (lexer.hasNext(type))
         return lexer.next();

      throw new ParserException(expected(type));
   }

   public void skip(TokenType type) throws ParserException {
      if (lexer.hasNext(type))
         lexer.skip();
      else
         throw new ParserException(expected(type));
   }

   private String expected(TokenType type) {
      return ("Parsing error: Expected " + type + " at line " + lexer.currentLine() + ", column " + lexer.nextColumn());
   }
}
