const express=require('express')
const app=express();
const multer=require('multer')
const upload=multer({dest:'upload/'})
const bodyParser=require('body-parser')
const fs=require('fs')
const exec=require('child_process').spawn
app.use(bodyParser.urlencoded({
   extended: false
}));
app.get('/',function (req,res,next) {
  res.sendFile(__dirname+'/index.html');
})
app.post('/upload',upload.single('file'),function (req,res,next) {
  res.send(req.file.filename);
})
app.post('/uploadhelper',bodyParser.json(),function (req,res,next) {
  fs.mkdir(__dirname+'/upload/'+req.body.timestamp,function () {
    fs.rename(__dirname+'/upload/'+req.body.filename,__dirname+'/upload/'+req.body.timestamp+'/data.zip',function () {
      res.send("ok")
    });
  })
})
app.post('/build',bodyParser.json(),function(req,res,next) {
   exec('python appgenserver.py '+req.body.timestamp);
   req.send("http://ENTER.IP.ADDRESS.OF.SERVER.HERE/release/"+req.body.timestamp+"/releaseapk.apk")
})
app.listen(2000)
