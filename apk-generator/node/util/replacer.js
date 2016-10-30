const jsonfile=require('jsonfile');
const replace=require('replace');
const Kefir=require('kefir').Kefir;
const configReplacer=function(config) {
	return Kefir.stream(function(emitter) {
jsonfile.writeFile(__dirname.split('apk-generator')[0]+'android/app/src/main/assets/config.json',config,function() {
      emitter.emit("changed");
	});

	});
};

const gradleReplacer=function(newtxt) {
	return Kefir.stream(function(emitter) {
		replace({
			regex:'"org\.fossasia\.openevent\..+"',
			replacement:'"org.fossasia.openevent.'+newtxt+'"',
			paths:[__dirname.split('apk-generator')[0]+'android/app/build.gradle']
		});
		emitter.emit("replaced");
	});
};
const appNameReplacer=function(appname) {
	return Kefir.stream(function(emitter) {
		replace({
			regex:'<string name="app_name" translatable="false">.*<\/string>',
			replacement:'<string name="app_name" translatable="false">'+appname+'</string>',
			paths:[__dirname.split('apk-generator')[0]+'android/app/src/main/res/values/strings.xml']
		});
		emitter.emit("replaced");
	});
};
exports.configReplacer=configReplacer;
exports.gradleReplacer=gradleReplacer;
exports.appNameReplacer=appNameReplacer;
