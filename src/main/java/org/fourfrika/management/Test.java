package org.fourfrika.management;

/*import com.ibm.icu.util.TimeZone;
import com.neovisionaries.i18n.CountryCode;
import com.neovisionaries.i18n.CurrencyCode;
import com.neovisionaries.i18n.LocaleCode;
import com.neovisionaries.i18n.ScriptCode;*/

import java.util.*;

public class Test {

    public static void main(String... args) {

        /**for (CountryCode code : CountryCode.values())
        {
            System.out.format("[%s] %s\n", code, code.getName());
        }

        printSpace();
         // List all the script codes.
        for (ScriptCode code : ScriptCode.values())
        {
            System.out.format("[%s] %03d %s\n", code, code.getNumeric(), code.getName());
        }

        printSpace();

        // List all the locale codes.
        for (LocaleCode code : LocaleCode.values())
        {
            String language = code.getLanguage().getName();
            String country  = code.getCountry() != null
                    ? code.getCountry().getName() : null;

            System.out.format("[%s] %s, %s\n", code, language, country);
        }

        printSpace();
// List all the currency codes.
        for (CurrencyCode code : CurrencyCode.values())
        {
            System.out.format("[%s] %03d %s\n", code, code.getNumeric(), code.getName());
        }

        String[] countryCodes = Locale.ENGLISH.getISOCountries();
        List<Country> list = new ArrayList<Country>(countryCodes.length);

        for (String cc : countryCodes) {
            list.add(new Country(cc.toUpperCase(), new Locale("", cc).getDisplayCountry()));
        }

        Collections.sort(list);

        for (Country c : list) {
            System.out.println(c.getName() );
            System.out.println(c.getCode() + "(\"" + c.getName() + "\"),");
        }**/

        //Arrays.stream(java.util.Locale.getISOLanguages()).forEach(System.out::println);

        //getAvailableTimeZones();

       /* Arrays.stream(Locale.getISOCountries()).forEach(  countryCode ->  {
                    for (String id : com.ibm.icu.util.TimeZone.getAvailableIDs(countryCode)){
                        System.out.println(countryCode + ": " + id);
                    }
                }
        );*/

    }

    public static Map<String, Set<TimeZone>> getAvailableTimeZones()
    {
        Map<String, Set<TimeZone>> availableTimezones =
                new HashMap<String, Set<TimeZone>>();
        // Loop through all available locales
        for (Locale locale : Locale.getAvailableLocales())
        {
            final String countryCode = locale.getCountry();
            // Locate the timezones added for this country so far
            // (This can be moved to inside the loop if depending
            // on whether country with no available timezones should
            // be in the result map with an empty set,
            // or not included at all)
            Set<TimeZone> timezones = availableTimezones.get(countryCode);
            if (timezones==null){
                timezones = new HashSet<TimeZone>();
                availableTimezones.put(countryCode, timezones);
            }
            // Find all timezones for that country (code) using ICU4J
            /*for (String id : com.ibm.icu.util.TimeZone.getAvailableIDs(countryCode)){
                System.out.println(countryCode + ": " + id);
                // Add timezone to result map
                timezones.add(TimeZone.getTimeZone(id));
            }*/
        }
        return availableTimezones;
    }

    private static void printSpace() {
        for (int i = 0; i < 10 ; i++) {
            System.out.println("");
        }
    }


    static class Country implements Comparable<Country> {
        private String code;
        private String name;

        public Country(String code, String name) {
            super();
            this.code = code;
            this.name = name;
        }


        public String getCode() {
            return code;
        }


        public void setCode(String code) {
            this.code = code;
        }


        public String getName() {
            return name;
        }


        public void setName(String name) {
            this.name = name;
        }


        @Override
        public int compareTo(Country o) {
            return this.name.compareTo(o.name);
        }
    }
}
