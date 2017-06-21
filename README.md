DownloadManager
=======

Java Console Application for downloading files.

Launching
--------
```
java -jar downloadmanager.jar -l link -p path -f file -t threads
```

Arguments
--------

* -l direct link to download
* -p path for saving downloaded file
* -f file of JSON, XML, CSV formats with links and paths
* -t count of threads
* --help help information

File Validate Forms
--------
JSON
```
[
    {
        "link": http://....
        "path": some_name.ext
    },
    {
        "link": http://....
        "path": another_name.ext
    }
    ...
]
```
XML
```
<root>
    <file>
        <link>http://...</link>
        <path>some_name.ext</path>
    </file>
    <file>
        <link>http://...</link>
        <path>another_name.ext</path>
    </file>
    ...
<root>
```
CSV
```
http://...;some_name.ext
http://...;another_name.ext
...
```

Output Example
--------------
```
|progress   |in process |downloaded |corrupted  |total      |speed      |time left  |
|     65.28%|          3|          7|          1|         15|   2.42MB/s|   00:07:23|
```

TableParser
=======

Java Console Application for effective finding table columns with necessary Regex/word.

Launching
--------
```
java -jar tableparser.jar -i in_file -o out_file -q regex/word
```

Arguments
--------

* -i file with table
* -o output file
* -q necessary Regex/word to find in table columns
* --help help information

File Validate Forms
--------
CSV
```
two;one;cat
two;girls;fox
result;grep;sometimes
fear;cap;one
```

Output Example
--------------
CSV
```
one;girls;grep;cap
cat;fox;sometimes;one
```
TXT
```
one
girls
grep
cap
cat
fox
sometimes
one
```
