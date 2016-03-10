#!/usr/bin/env bash

# Copyright (c) 2015 noboru-i
# https://github.com/noboru-i/android-saddler-sample/blob/master/scripts/saddler.sh

echo "********************"
echo "* install gems     *"
echo "********************"
gem install --no-document saddler-reporter-text checkstyle_filter-git saddler saddler-reporter-github findbugs_translate_checkstyle_format android_lint_translate_checkstyle_format pmd_translate_checkstyle_format

if [ $? -ne 0 ]; then
    echo 'Failed to install gems.'
    exit 1
fi

echo "********************"
echo "* exec gradle      *"
echo "********************"
./gradlew app:check

if [ $? -ne 0 ]; then
    echo 'Failed gradle check task.'
    exit 1
fi

echo "********************"
echo "* save outputs     *"
echo "********************"

# Only applicable to Circle CI
#LINT_RESULT_DIR="$CIRCLE_ARTIFACTS/lint"
#
#mkdir "$LINT_RESULT_DIR"
#cp -v "app/build/reports/checkstyle/checkstyle.xml" "$LINT_RESULT_DIR/"
#cp -v "app/build/reports/findbugs/findbugs.xml" "$LINT_RESULT_DIR/"
#cp -v "app/build/outputs/lint-results.xml" "$LINT_RESULT_DIR/"

if [ -z "${CI_PULL_REQUEST}" ]; then
    # when not pull request
    REPORTER=Saddler::Reporter::Github::CommitReviewComment
    REPORTER_REQUIRE=saddler/reporter/github
#    REPORTER=Saddler::Reporter::Text
#    REPORTER_REQUIRE=saddler/reporter/text
else
    REPORTER_REQUIRE=saddler/reporter/github
    REPORTER=Saddler::Reporter::Github::PullRequestReviewComment
fi

echo "********************"
echo "* checkstyle       *"
echo "********************"
cat app/build/reports/checkstyle/checkstyle.xml \
    | saddler report --require ${REPORTER_REQUIRE} --reporter ${REPORTER}
#    | checkstyle_filter-git diff origin/master \

echo "********************"
echo "* findbugs         *"
echo "********************"
cat app/build/reports/findbugs/findbugs.xml \
    | findbugs_translate_checkstyle_format translate \
    | saddler report --require ${REPORTER_REQUIRE} --reporter ${REPORTER}
#    | checkstyle_filter-git diff origin/master \

echo "********************"
echo "* PMD              *"
echo "********************"
cat app/build/reports/pmd/pmd.xml \
    | pmd_translate_checkstyle_format translate \
    | saddler report --require ${REPORTER_REQUIRE} --reporter ${REPORTER}
#    | checkstyle_filter-git diff origin/master \

echo "********************"
echo "* PMD-CPD          *"
echo "********************"
cat app/build/reports/pmd/cpd.xml \
    | pmd_translate_checkstyle_format translate --cpd-translate \
    | saddler report --require ${REPORTER_REQUIRE} --reporter ${REPORTER}
#    | checkstyle_filter-git diff origin/master \

echo "********************"
echo "* android lint     *"
echo "********************"
if [ -z "$TRAVIS" ]; then
  # If not on travis
  cat app/build/outputs/lint-results-fdroidDebug.xml \
      | android_lint_translate_checkstyle_format translate \
      | saddler report --require ${REPORTER_REQUIRE} --reporter ${REPORTER}
  #    | checkstyle_filter-git diff origin/master \
fi
