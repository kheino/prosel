package io.heino.prosel.internal;

public enum TokenType {
   LBRACE(1, "opening '{'"),
   RBRACE(1, "closing '}'"),
   IDENT("identifier"),
   INT("integer"),
   COLON(1, "':'"),
   COMMA(1, "','"),
   ASTER(1, "'*'"),
   QUEST(1, "'?'"),
   EXCLA(1, "'!'");

   private int len;
   private String desc;

   TokenType(String desc) {
      this(0, desc);
   }

   TokenType(int len, String desc) {
      this.len = len;
      this.desc = desc;
   }

   public int getLength() {
      return len;
   }

   @Override
   public String toString() {
      return desc;
   }
}
