package io.heino.prosel;

public class LexerException extends Exception {
   public LexerException(String msg) {
      super(msg);
   }

   public LexerException(Throwable reason) {
      super(reason);
   }
}
