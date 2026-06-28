# BuildABrowser (Draft)

BuildABrowser is a project which aims to guide users through creating a relatively
basic web browser rendering engine.

Draft: Current focus is on confirming BAB's architecture and drafting the step order.
This will be rewritten for the final version of BuildABrowser.
Writing the relevant lessons and filming relevant videos is not a goal of this draft.
This draft will later be discarded in order to create the final version.

Similar to existing resources like https://browser.engineering/, BuildABrowser aims
to teach those interested in browser development the concepts they need (eg DOM,
CSSOM, HTML parsing, and so on) in order to build a browser (as the name suggests).

Where BuildABrowser differs from existing tutorials is that it also, simultaneously,
aims for a degree of spec-conformance and compatibility with existing web pages.

BuildABrowser is meant to have a step-by-step commit history. What this means is that,
in addition to reading any documentation that will be written, you can also observe
the diff of two particular steps (ideal for people who can read code quickly).

Though this is a draft (and as such does not include them), the final version of
BuildABrowser is planned to have accompanying videos and articles. 

BuildABrowser is informed by the author's previous experience writing the Webicity
Web Browser. However, it is not a clone of Webicity. Instead, it aims to resolve a
number of the architectural issues that prevented the scaling of Webicity (for
example, it focuses more on the layout context than element composition, reducing
the OOP indirection for routing data. It aims to be specific-built for the web
instead of attempting to double as a general-purpose UI engine). Note that, in many
cases were Webicity used OOP design patterns, BAB opts to instead use pattern-
matching switch.

BuildABrowser will not include some elements that Webicity included (such as the
custom http parser and the image codecs) in order to simplify it for prospective
learners. There may be a BAB-based Webicity-branded fork in the future.

## Running BuildABrowser

```
./gradlew run --args="file:///path/to/your/file"
```

As BuildABrowser is in an early state of development, it is not anticipated that
many HTTP resources will work, unless they are designed with early-stage BAB
limitations in mind.

## Running BuildABrowser Tests

```
./gradlew test
```

Because BuildABrowser is a multi-modular project, the tests maybe be split across
build folders. Gradle should tell you where you can find a report for failing
builds.