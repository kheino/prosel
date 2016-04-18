package io.heino.prosel.internal;

public class ValueToken extends Token {
   private String value;

   public ValueToken(TokenType type, int line, int col, String value) {
      super(type, line, col);
      this.value = value;
   }

   public String getValue() {
      return value;
   }

   @Override
   public int getLength() {
      return value.length();
   }
}
