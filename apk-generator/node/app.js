const express=require('express');
const app=express();
const multer=require('multer');
const upload=multer({dest:'upload/'});
const bodyParser=require('body-parser');
const fs=require('fs');
const replacer=require('./util/replacer.js');
const firebase=require('./firebase/model.js');
const build=require('./util/build.js');
app.use(bodyParser.urlencoded({
   extended: false
}));

app.get('/',function (req,res,next) {
  res.sendFile(__dirname+'/index.html');
});

app.post('/upload',upload.single('file'),function (req,res,next) {
  res.send(req.file.filename);
});

app.post('/uploadhelper',bodyParser.json(),function (req,res,next) {
  fs.mkdir(__dirname+'/upload/'+req.body.timestamp,function () {
    fs.rename(__dirname+'/upload/'+req.body.filename,__dirname+'/upload/'+req.body.timestamp+'/data.zip',function () {
      res.send("ok");
    });
  });
});

app.post('/build',bodyParser.json(),function(req,res,next) {
userData=firebase.userData(req.body.timestamp);
userData.onValue(function (data) {
  result=replacer.configReplacer(data).flatMap(function (x) {
    console.log(x);
    return replacer.gradleReplacer(data.App_Name.split(' ')[0]);
  }).flatMap(function (x) {
    console.log(x);
    return replacer.appNameReplacer(data.App_Name);
  }).flatMap(function (x) {
    console.log(x);
    return build.buildApk(req.body.timestamp);
  });

  result.onValue(function (x) {
   res.send("release/"+req.body.timestamp);
  });

});
});

app.get('/release/:id',function (req,res,next) {
res.download(__dirname+'/upload/'+req.params.id+'/releaseapk.apk');
});

app.listen(2000);
