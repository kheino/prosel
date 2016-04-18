package io.heino.prosel.internal;

public class Token {
   private TokenType type;
   private int line, col;

   protected Token(TokenType type, int line, int col) {
      this.type = type;
      this.line = line;
      this.col = col;
   }

   public TokenType getType() {
      return type;
   }

   public int getLine() {
      return line;
   }

   public int getColumn() {
      return col;
   }

   public int getLength() {
      return type.getLength();
   }
}
