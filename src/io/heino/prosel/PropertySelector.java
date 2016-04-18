package io.heino.prosel;

import io.heino.prosel.internal.Parser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PropertySelector {
   private UniversalSelector us;
   private Set<String> excludes;
   private Map<String, PropertySelector> selectors;

   public PropertySelector() {
      this(UniversalSelector.NONE);
   }

   public PropertySelector(UniversalSelector us) {
      excludes = new HashSet<>();
      selectors = new HashMap<>();

      setUniversalSelector(us);
   }

   public static PropertySelector parse(String selector) throws ParserException {
      return new Parser().parse(selector);
   }

   public UniversalSelector getUniversalSelector() {
      return us;
   }

   public void setUniversalSelector(UniversalSelector us) {
      this.us = us;
   }

   public void add(String name) {
      add(name, new PropertySelector(UniversalSelector.ALL));
   }

   public void add(String name, PropertySelector ps) {
      selectors.put(name, ps);
   }

   public PropertySelector getSelector(String name) {
      if (selectors.containsKey(name)) {
         return selectors.get(name);
      } else if (!isExcluded(name)) {
         return new PropertySelector(UniversalSelector.ALL);
      } else {
         return new PropertySelector();
      }
   }

   public boolean isSelected(String name) {
      return (selectors.containsKey(name) || !isExcluded(name));
   }

   public void exclude(String name) {
      excludes.add(name);
   }

   public boolean isExcluded(String name) {
      return (us.isExclude() || excludes.contains(name));
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("{ ");
      sb.append(us);

      for (String excl : excludes) {
         sb.append(", !");
         sb.append(excl);
      }

      for (Map.Entry<String, PropertySelector> entry : selectors.entrySet()) {
         sb.append(", ");
         sb.append(entry.getKey());
         sb.append(": ");
         sb.append(entry.getValue());
      }

      sb.append(" }");

      return sb.toString();
   }
}
