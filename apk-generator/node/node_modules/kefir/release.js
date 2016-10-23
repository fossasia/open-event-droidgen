var inquirer = require('inquirer');
var semver = require('semver');
var shell = require('shelljs');
var fs = require('fs');

var pkg = require('./package.json');
var bower = require('./bower.json');

console.log("");
console.log("Wellcome to Kefir release utility!");
console.log("----------------------------------------------------------------");
console.log("");

var questions = [
  {
    type: "list",
    name: "version",
    message: "Which version will it be? (current is " + pkg.version + ")",
    choices: [
      semver.inc(pkg.version, 'patch'),
      semver.inc(pkg.version, 'minor'),
      semver.inc(pkg.version, 'major'),
      semver.inc(pkg.version, 'premajor', 'rc')
    ]
  },
  {
    type: "list",
    name: "dryRun",
    message: "Do you want to release, or just see what would happen if you do?",
    choices: [
      'Just see',
      'Release!'
    ]
  }
];

inquirer.prompt(questions, function(answers) {
  var newVerison = answers.version;
  var dryRun = (answers.dryRun === 'Just see');

  bower.version = pkg.version = newVerison;

  console.log('');
  if (dryRun) {
    console.log('Ok, here is what would happen:');
  } else {
    console.log('Doing actual release:');
  }
  console.log('');

  run('npm test', dryRun) &&
  bumpVersion('package.json', pkg, dryRun) &&
  bumpVersion('bower.json', bower, dryRun) &&
  run('`npm bin`/grunt', dryRun) &&
  run('`npm bin`/grunt bower', dryRun) &&
  run('git add .', dryRun) &&
  run('git add -f dist', dryRun) &&
  run('git add -f index.html', dryRun) &&
  run('git add -f bower-packages', dryRun) &&
  run('git add -f test/in-browser/spec/KefirSpecs.js', dryRun) &&
  run('git commit -m "' + newVerison + '"', dryRun) &&
  run('git push', dryRun) &&
  run('git tag -a ' + newVerison + ' -m "v' + newVerison + '"', dryRun) &&
  run('git push origin --tags', dryRun) &&
  run('npm publish', dryRun) &&
  run('git rm -r dist', dryRun) &&
  run('git rm -r bower-packages', dryRun) &&
  run('git rm index.html', dryRun) &&
  run('git rm test/in-browser/spec/KefirSpecs.js', dryRun) &&
  run('git commit -m "cleanup repository after release"', dryRun) &&
  run('git push', dryRun);

});

function bumpVersion(fileName, obj, dry) {
  console.log('Bumping version in `' + fileName + '` to ' + obj.version);
  if (!dry) {
    try {
      fs.writeFileSync(fileName, JSON.stringify(obj, null, '  ') + '\n');
      console.log('... ok');
    } catch(e) {
      console.error(e);
      return false;
    }
  }
  return true;
}

function run(cmd, dry){
  console.log('Running `' + cmd + '`');
  if (!dry) {
    if (shell.exec(cmd, {silent:false}).code === 0){
      console.log('... ok');
    } else{
      console.error('... fail!');
      return false;
    }
  }
  return true;
}
