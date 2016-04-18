package io.heino.prosel;

public class UniversalSelector {
   public static final UniversalSelector
         NONE = new UniversalSelector(-1),
         ALL = new UniversalSelector(0),
         BLOCK = new UniversalSelector(1);

   private final int limit;

   public UniversalSelector(int limit) {
      this.limit = limit;
   }

   public boolean isExclude() {
      return (limit == -1);
   }

   public UniversalSelector descend() {
      switch (limit) {
         case -1:
         case 1:
            return NONE;
         case 0:
            return ALL;
         case 2:
            return BLOCK;
         default:
            return new UniversalSelector(limit - 1);
      }
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this)
         return true;

      if (!(obj instanceof UniversalSelector))
         return false;

      UniversalSelector other = (UniversalSelector)obj;
      return (limit == other.limit);
   }

   @Override
   public int hashCode() {
      return limit;
   }

   @Override
   public String toString() {
      switch (limit) {
         case -1:
            return "!*";
         case 0:
            return "**";
         case 1:
            return "*?";
         default:
            return ("*" + limit);
      }
   }
}
