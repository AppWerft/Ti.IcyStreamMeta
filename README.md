Ti.IcyStreamMeta
================

This is a Titanium module fort getting the radiotext.

Usage
-----
```javascript
var metaModule = require("de.appwerft.icystreammeta");
var metaClient = metaModule.createIcyClient({
    url : "http://hr-mp3-m-h2.akacast.akamaistream.net/7/786/142132/v1/gnl.akacast.akamaistream.net/hr-mp3-m-h2",
    autoStart : true,
    pullInterval : 5, // sec.
    charset : "UTF-8", // default optional
    onload : function(_e) {
        console.log(_e);
    },
    onerror : function(_e) {
        console.log(_e);
    }
});

metaClient.start();

setTimeout(function() {
    metaClient.stop();
},1000*60);


```


###Methods

