package io.heino.prosel.internal;

import io.heino.prosel.LexerException;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

public class Lexer {
   private static final char
         LBRACE = '{',
         RBRACE = '}',
         COLON = ':',
         COMMA = ',',
         ASTER = '*',
         QUEST = '?',
         EXCLA = '!';

   private List<Token> tokens;
   private int cursor;

   private Reader reader;
   private StringBuilder buffer;
   private int line, col;
   private boolean eof;
   private char c;

   public Lexer() {
      tokens = new ArrayList<>();
      cursor = -1;
   }

   public void analyze(String input) throws LexerException {
      try {
         reader = new StringReader(input);
         buffer = new StringBuilder();
         line = 1;
         col = 0;
         eof = false;

         while (reader.ready()) {
            read();

            if (eof)
               break;

            if (isWhitespace())
               continue;

            switch (c) {
               case LBRACE:
                  add(TokenType.LBRACE);
                  continue;
               case RBRACE:
                  add(TokenType.RBRACE);
                  continue;
               case COLON:
                  add(TokenType.COLON);
                  continue;
               case COMMA:
                  add(TokenType.COMMA);
                  continue;
               case ASTER:
                  add(TokenType.ASTER);
                  continue;
               case QUEST:
                  add(TokenType.QUEST);
                  continue;
               case EXCLA:
                  add(TokenType.EXCLA);
                  continue;
            }

            if (readToken(TokenType.IDENT, this::isWord))
               continue;

            if (readToken(TokenType.INT, this::isDigit))
               continue;

            throw new LexerException("Lexical error: Invalid character at line " + line + ", column " + col + ": " + c);
         }
      } catch (IOException e) {
         throw new LexerException(e);
      } finally {
         reader = null;
         buffer = null;
      }
   }

   private boolean isWord() {
      return (c == '_' || isAlpha() || isDigit());
   }

   private boolean isAlpha() {
      return ('A' <= c && c <= 'Z') || ('a' <= c && c <= 'z');
   }

   private boolean isDigit() {
      return ('0' <= c && c <= '9');
   }

   private boolean isWhitespace() {
      switch (c) {
         case '\n':
            eol();
         case ' ':
         case '\t':
         case '\u000B': // Vertical tab
         case '\f':
         case '\r':
            return true;
         default:
            return false;
      }
   }

   private void eol() {
      line++;
      col = 0;
   }

   private void read() throws IOException {
      int i = reader.read();
      if (i == -1) {
         eof = true;
         c = '\0';
      } else {
         col++;
         c = (char)i;
      }
   }

   private boolean readToken(TokenType type, BooleanSupplier is)
         throws LexerException {

      try {
         buffer.setLength(0);

         while (!eof && is.getAsBoolean()) {
            buffer.append(c);
            reader.mark(0);
            read();
         }

         if (buffer.length() == 0)
            return false;

         add(type, buffer.toString());

         // Rewind one char
         reader.reset();
         col--;

         return true;
      } catch (IOException e) {
         throw new LexerException(e);
      }
   }

   private void add(TokenType type) {
      tokens.add(new Token(type, line, (col - type.getLength())));
   }

   private void add(TokenType type, String value) {
      tokens.add(new ValueToken(type, line, (col - value.length()), value));
   }

   public boolean hasNext(TokenType type) {
      return (cursor <= (tokens.size() - 2) && tokens.get(cursor + 1).getType() == type);
   }

   public Token next() {
      return tokens.get(++cursor);
   }

   public void skip() {
      cursor++;
   }

   public boolean trySkip(TokenType type) {
      if (!hasNext(type))
         return false;

      skip();
      return true;
   }

   public int currentLine() {
      return tokens.get(cursor).getLine();
   }

   public int nextColumn() {
      Token t = tokens.get(cursor);
      return (t.getColumn() + t.getLength());
   }
}
