
const process=require('process');
process.chdir(__dirname.split('util')[0]);
const spawn = require('child_process').spawn;

const Kefir=require('kefir').Kefir;
const fs=require('fs');
const mv=require('mv');
const buildApk=function (uid) {
return Kefir.stream(function (emitter) {
  const build = spawn('./buildApk.sh');
  build.stdout.on('data', (data) => {
    console.log(`stdout: ${data}`);
  });

  build.stderr.on('data', (data) => {
    console.log(`stderr: ${data}`);
  });
  build.on('error',function (err) {
    console.log(err);
  });
  build.on('close', (code) => {
    console.log(`child process exited with code ${code}`);
    mv(__dirname.split('apk-generator')[0]+'android/app/build/outputs/apk/app-googleplay-release-unsigned.apk',__dirname.split('node')[0]+'node/upload/'+uid+'/releaseapk.apk',function (err) {
      console.log(err);
      emitter.emit('build done');
    })

  });
});
}
exports.buildApk=buildApk;
;