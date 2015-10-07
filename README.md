# wire

NoMoDEI fork for the wire project

## Prerequisites

You will need [Leiningen][1] 1.7.0 or above installed.

[1]: https://github.com/technomancy/leiningen

## Running

To test the monitor norm parser, open a terminal and run the following commands:
```javascript
lein run -m wire.test.monitor "parse" "TestOpera.opera"
```
To test the monitor inference engine, open a terminal and run the following commands:
```javascript
lein run -m wire.test.monitor "engine" "TestOpera.opera"
```

To test the monitor dynamic inference engine, open a terminal and run the following commands:
```javascript
lein run -m wire.test.dynamic "plain"
```
```javascript
lein run -m wire.test.dynamic "propespective"
```


## License
Ignasi Gómez-Sebastià
Copyright © 2014 FIXME
