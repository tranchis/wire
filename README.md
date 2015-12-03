# wire

Normative monitor, plus:

 * NoMoDEI

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

To test the monitor dynamic inference engine, open a terminal and run one of the following commands:
```javascript
lein run -m wire.test.dynamic "plain"
```
```javascript
lein run -m wire.test.dynamic "propespective"
```
```javascript
lein run -m wire.test.dynamic "basic-water"
```
```javascript
lein run -m wire.test.dynamic "propespective-water"
```
```javascript
lein run -m wire.test.dynamic "retroactive-water"
```
```javascript
lein run -m wire.test.dynamic "annulment-water"
```
```javascript
lein run -m wire.test.dynamic "abrogation-water"
```




## Changelog
01-06-2015 First version
01-07-2015 Refined version with use cases
01-11-2015 Improving code and completing use cases
28-11-2015 Cosmetic improving of code

## License
Sergio Alvarez-Napagao
Ignasi Gómez-Sebastià
Copyright © 2014 FIXME
