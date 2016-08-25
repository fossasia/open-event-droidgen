# Code Style Guidelines for Open-Event-Android

Our code style guidelines is based on the [Android Code Style Guidelines for Contributors](https://source.android.com/source/code-style.html).

Do take some time to read it.

Only a few extra rules:

- Line length is 120 characters
- FIXME must not be committed in the repository use TODO instead. FIXME can be used in your own local repository only.

You can run a checkstyle with most rules via a gradle command:

```
$ ./gradlew checkstyle
```

It generates a HTML report in `build/reports/checkstyle/checkstyle.html`.

Try to remove as much warnings as possible, It's not completely possible to remove all the warnings, but over a period of time, we should try to make it as complete as possible.

Some **DONT's**

- Don't use Hungarian Notation like `mContext` `mCount` etc
- Don't use underscores in variable names
- All constants should be CAPS. e.g `MINIMUM_TIMEOUT_ERROR_EXTERNAL`
- Always use `Locale.ENGLISH` when using `String.format()` unless the format itself is locale dependent e.g. `String query = String.format(Locale.ENGLISH,...`
- Never concat `null` with `""` (Empty String). It will become `"null"` e.g. `String.equals("" + null, "null") == TRUE`

Optionally if you want to make life easier, you can download the Styles Settings Jar from [here](https://dl.dropboxusercontent.com/u/10123399/fossasia_codestyle.jar) and use autoformat before submission.
