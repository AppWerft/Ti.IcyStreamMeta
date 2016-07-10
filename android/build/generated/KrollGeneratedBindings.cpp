/* C++ code produced by gperf version 3.0.3 */
/* Command-line: /Applications/Xcode.app/Contents/Developer/Toolchains/XcodeDefault.xctoolchain/usr/bin/gperf -L C++ -E -t /private/var/folders/25/yvdn9h053sv3htdkzq8558k40000gn/T/fuerst/Icymetaclient-generated/KrollGeneratedBindings.gperf  */
/* Computed positions: -k'' */

#line 3 "/private/var/folders/25/yvdn9h053sv3htdkzq8558k40000gn/T/fuerst/Icymetaclient-generated/KrollGeneratedBindings.gperf"


#include <string.h>
#include <v8.h>
#include <KrollBindings.h>

#include "de.appwerft.icymetaclient.IcyMetaClientProxy.h"
#include "de.appwerft.icymetaclient.IcymetaclientModule.h"


#line 14 "/private/var/folders/25/yvdn9h053sv3htdkzq8558k40000gn/T/fuerst/Icymetaclient-generated/KrollGeneratedBindings.gperf"
struct titanium::bindings::BindEntry;
/* maximum key range = 2, duplicates = 0 */

class IcymetaclientBindings
{
private:
  static inline unsigned int hash (const char *str, unsigned int len);
public:
  static struct titanium::bindings::BindEntry *lookupGeneratedInit (const char *str, unsigned int len);
};

inline /*ARGSUSED*/
unsigned int
IcymetaclientBindings::hash (register const char *str, register unsigned int len)
{
  return len;
}

struct titanium::bindings::BindEntry *
IcymetaclientBindings::lookupGeneratedInit (register const char *str, register unsigned int len)
{
  enum
    {
      TOTAL_KEYWORDS = 2,
      MIN_WORD_LENGTH = 44,
      MAX_WORD_LENGTH = 45,
      MIN_HASH_VALUE = 44,
      MAX_HASH_VALUE = 45
    };

  static struct titanium::bindings::BindEntry wordlist[] =
    {
      {""}, {""}, {""}, {""}, {""}, {""}, {""}, {""}, {""},
      {""}, {""}, {""}, {""}, {""}, {""}, {""}, {""}, {""},
      {""}, {""}, {""}, {""}, {""}, {""}, {""}, {""}, {""},
      {""}, {""}, {""}, {""}, {""}, {""}, {""}, {""}, {""},
      {""}, {""}, {""}, {""}, {""}, {""}, {""}, {""},
#line 16 "/private/var/folders/25/yvdn9h053sv3htdkzq8558k40000gn/T/fuerst/Icymetaclient-generated/KrollGeneratedBindings.gperf"
      {"de.appwerft.icymetaclient.IcyMetaClientProxy", ::de::appwerft::icymetaclient::icymetaclient::IcyMetaClientProxy::bindProxy, ::de::appwerft::icymetaclient::icymetaclient::IcyMetaClientProxy::dispose},
#line 17 "/private/var/folders/25/yvdn9h053sv3htdkzq8558k40000gn/T/fuerst/Icymetaclient-generated/KrollGeneratedBindings.gperf"
      {"de.appwerft.icymetaclient.IcymetaclientModule", ::de::appwerft::icymetaclient::IcymetaclientModule::bindProxy, ::de::appwerft::icymetaclient::IcymetaclientModule::dispose}
    };

  if (len <= MAX_WORD_LENGTH && len >= MIN_WORD_LENGTH)
    {
      unsigned int key = hash (str, len);

      if (key <= MAX_HASH_VALUE)
        {
          register const char *s = wordlist[key].name;

          if (*str == *s && !strcmp (str + 1, s + 1))
            return &wordlist[key];
        }
    }
  return 0;
}
#line 18 "/private/var/folders/25/yvdn9h053sv3htdkzq8558k40000gn/T/fuerst/Icymetaclient-generated/KrollGeneratedBindings.gperf"

