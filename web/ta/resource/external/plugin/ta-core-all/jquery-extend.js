/**
 * javasrcipt,juqery的一些扩展方法
 */
String.prototype.startWith = function(str) {
	var reg = new RegExp("^" + str);
	return reg.test(this);
};

String.prototype.endWith = function(str) {
	var reg = new RegExp(str + "$");
	return reg.test(this);
};
/*
 * jQuery.extend({
 * 
 * });
 */