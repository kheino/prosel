package io.heino.prosel;

public class ParserException extends Exception {
   public ParserException(String msg) {
      super(msg);
   }

   public ParserException(Throwable reason) {
      super(reason);
   }
}
