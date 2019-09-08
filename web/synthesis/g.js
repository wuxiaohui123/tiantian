function uptAOlData(formid){   
	var o3;
	window.formdata = new Object({});
	o3 = "string" == typeof formid && $("#" + formid)[0];
	o3 = $(o3).find(":input"); 
	 if(o3 && 0 < o3.length){
         o3.each(function(){
            if(this.name){
            	if( this.type!="button" && this.type!="checkbox" && this.name != "dto['isTab']") {
            		formdata[this.name]={};
            		formdata[this.name]={
            			desc:$(this).parent().parent("div").find("label").text().replace(/^\s*\**/, ""),
            			val:this.value,
            		    sel_desc:this.value||null,
            			checked:this.checked
            		};
            	}
            }
         });
	 }
	 return formdata;
}
function getAllchgData(szId){
	var oData = window.formdata,o3,chgData = {},reData="";
			 o3 = "string" == typeof szId && $("#" + szId)[0] || $(szId)[0];
			 o3 = $(o3).find(":input");var chk,oChk,isChk=false;
			 if(o3 && 0 < o3.length)
             o3.each(function(){
			 	if (this.name) {
			 	try{chk = this.checked;}catch(e){chk = false;}
			 	try{oChk = oData[this.name]["checked"];}catch(e){oChk = false;}
			 	if(this.type=="checkbox" || this.type=="radio") isChk = true;
			 		if(this.type!="button"){
						if (null != oData[this.name] && ((isChk && chk != oChk) || (this.value != oData[this.name].val))){
							chgData[this.name] = {
									o_desc: oData[this.name]&&oData[this.name]["desc"] || null,  
									n_desc: $(this).parent().parent("div").find("label").text().replace(/^\s*\**/, ""), 
									o_val: oData[this.name]&&oData[this.name]["val"] || null, 
									n_val: this.value, 
									o_sel_desc: oData[this.name]&&oData[this.name]["sel_desc"] || null, 
									n_sel_desc: this.value|| null 
								};
							var name=this.name;
							var str=name.substring (5,name.length-2);
							var oldval=oData[this.name]&&oData[this.name]["val"] || null;
							reData+="{'param':'"+str+"','_new':'"+this.value+"','_old':'"+oldval+"'},";
						} 
			 		}
			 	}
			 });
	if (reData != "") {
		reData = "[" + reData.substr(0, reData.length - 1) + "]";
	}
	return reData;
}
//$(this).parent().find("input[type='text']").attr("id")+"值为:"+
//"{'"+str+"':'"+this.value+"','"+str+"_old':'"+oldval+"'},"