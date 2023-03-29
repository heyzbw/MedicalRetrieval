import $ from 'jquery'
var i = 1;
$(function () {

	var navBox = $('.tabTagBox'), navList = $('.tabTagList'), navs = navList.children('li'), upBtn = $('.uPrev'), downBtn = $('.dNext'), contentBoxs = $('.tabcon');
	var opts = {
		moveH: 58,
		moveSpeed: 200,
		curMoveH: 0,
		curSumH: 0,
		curNavIndex: 0
	}
	opts.curSumH = (opts.moveH * navs.size()) - navBox.height();
	var navToContentBox = function () {
		navs.removeClass('current');
		contentBoxs.hide().eq(opts.curNavIndex).show();
	}
	navs.click(function () {
		opts.curNavIndex = $(this).index();
		navToContentBox();
		if (i == 1) {
			//执行顺序
			/*获取左侧分类列表*/
			getmess();
			getzycate("jb", "0");
			getletter();
			getdhlist();
			i++;
		}
		$(this).addClass('current');
		//lazyloadForPart(contentBoxs);
	});
});


function islogin() {
	var mess = $("#mess").val();
	if (mess != "1") {
		layer.msg(mess);
		return true;
		//		layer.alert(mess);
	} else {
		return false;
	}
}

/*全部*/
$(function () {
	//两项查询
	queryAsso();
	//意见检索前复选框选择事件
	getzytype("");
	zytypeclick();
	//查询单个热点
	serachhotOne();
	//查询所有热点
	serachhot();
	//查询最近更新
	serachrecentOne();
	serachrecent();
	//点击一键检索
	serachcommon();
	//高级检索加载
	iningj(1);
	clickgjzytype();
	gaojiserach();

})
//查询框前选择资源类型选择变化
function zytypeclick() {
	$(".subNav").click(function () {
		$(this).toggleClass("currentDd").siblings(".subNav").removeClass("currentDd")
		$(this).toggleClass("currentDt").siblings(".subNav").removeClass("currentDt")

		// 修改数字控制速度， slideUp(500)控制卷起速度
		$(this).next(".navContent").slideToggle(500).siblings(".navContent").slideUp(500);
	})
	$(".subNav").blur(function () {
		$(this).next(".navContent").slideUp(500);
	});
}
function funcChina(str) {

	if (/.*[\u4e00-\u9fa5]+.*$/.test(str)) {
		return true;
		//不是中文
	}
	return false;
}
/*联想输入*/
function queryAsso() {
	$(".subNav-ipt").click(function () {
		var commonSerachss = $("#commonSerach").val();
		if (commonSerachss != "" && commonSerachss.length > 2 && funcChina(commonSerachss)) {
			searchlx();
			$(this).next(".navContent-ipt").slideToggle(500).siblings(".navContent-ipt").slideUp(500);
		}

	});
	$("#commonSerach").bind("input propertychange", function () {
		var commonSerachss = $("#commonSerach").val();
		if (commonSerachss != "" && commonSerachss.length > 2 && funcChina(commonSerachss)) {
			searchlx();
			$(this).next(".navContent-ipt").slideDown(500);
		} else {
			$(".subNav-ipt").next(".navContent-ipt").slideUp(500);
		}

	});
	$("#commonSerach").blur(function () {
		$(".subNav-ipt").next(".navContent-ipt").slideUp(500);
	});
}

/*检索推荐*/
function searchlx() {
	//debugger;
	var commonSerach = $("#commonSerach").val();
	var zytype = $("#zyType").val();
	var keyWords = "jb,jc,yw,zz,ss";
	if (zytype != "all") {
		keyWords = zytype;
	}
	var htmls = "";
	for (var i = 0; i < keyWords.split(",").length; i++) {
		var tableType = keyWords.split(",")[i];
		url = "/front/date/pagelist";
		var dataJson = '{commonSerach:"' + commonSerach + '",draw: "1",currpage: "0",pagesize: "10",serachType: "common",tableType: "' + tableType + '"}';
		$.ajax({
			type: 'post', //数据发送方式  
			dataType: 'text', //接受数据格式  
			url: url,
			async: false,
			data: dataJson,
			success: function (result) {
				result = JSON.parse(result);;
				if (result.data.length > 0) {
					htmls += "<div class='related-words'>";
					if (tableType == "jb") {
						htmls += "<h3>疾病</h3>";
					} else if (tableType == "jc") {
						htmls += "<h3>检查</h3>";
					} else if (tableType == "zz") {
						htmls += "<h3>症状</h3>";
					} else if (tableType == "ss") {
						htmls += "<h3>手术</h3>";
					} else if (tableType == "yw") {
						htmls += "<h3>药物</h3>";
					} else if (tableType == "blbg") {
						htmls += "<h3>病例报告</h3>";
					} else if (tableType == "hzxz") {
						htmls += "<h3>患者教育</h3>";
					} else if (tableType == "zlzn") {
						htmls += "<h3>诊疗指南</h3>";
					}
					htmls += "<ul>";
					for (var s = 0; s < result.data.length; s++) {
						if (s == result.data.length - 1) {
							if (tableType == "jb") {
								htmls += "<li><a href='#'>" + result.data[s].DICHINESENAME + "</a><span></span></li>";
							} else if (tableType == "jc") {
								htmls += "<li><a href='#'>" + result.data[s].EXCHINESENAME + "</a><span></span></li>";
							} else if (tableType == "zz") {
								htmls += "<li><a href='#'>" + result.data[s].SYCHINESENAME + "</a><span></span></li>";
							} else if (tableType == "ss") {
								htmls += "<li><a href='#'>" + result.data[s].OPCHINESENAME + "</a><span></span></li>";
							} else if (tableType == "yw") {
								htmls += "<li><a href='#'>" + result.data[s].CHINESENAME + "</a><span></span></li>";
							} else if (tableType == "blbg") {
								htmls += "<li class='w100'><a href='#'>" + result.data[s].CRTITLE + "</a><span></span></li>";
							} else if (tableType == "hzxz") {
								htmls += "<li><a href='#'>" + result.data[s].PETITLE + "</a><span></span></li>";
							} else if (tableType == "zlzn") {
								htmls += "<li class='w100'><a href='#'>" + result.data[s].TGTITLE + "</a><span></span></li>";
							}
						} else {
							if (tableType == "jb") {
								htmls += "<li><a href='#'>" + result.data[s].DICHINESENAME + "</a><span> | </span></li>";
							} else if (tableType == "jc") {
								htmls += "<li><a href='#'>" + result.data[s].EXCHINESENAME + "</a><span> | </span></li>";
							} else if (tableType == "zz") {
								htmls += "<li><a href='#'>" + result.data[s].SYCHINESENAME + "</a><span> | </span></li>";
							} else if (tableType == "ss") {
								htmls += "<li><a href='#'>" + result.data[s].OPCHINESENAME + "</a><span> | </span></li>";
							} else if (tableType == "yw") {
								htmls += "<li><a href='#'>" + result.data[s].CHINESENAME + "</a><span> | </span></li>";
							} else if (tableType == "blbg") {
								htmls += "<li class='w100'><a href='#'>" + result.data[s].CRTITLE + "</a></li>";
							} else if (tableType == "hzxz") {
								htmls += "<li><a href='#'>" + result.data[s].PETITLE + "</a><span> | </span></li>";
							} else if (tableType == "zlzn") {
								htmls += "<li class='w100'><a href='#'>" + result.data[s].TGTITLE + "</a></li>";
							}
						}

					}
					htmls += "</ul></div>";

				}
			}
		});

	}
	if (commonSerach == "") {
		htmls = "";
	}
	$(".navContent-ipt").html(htmls);
	$(".navContent-ipt").find("a").each(function () {
		$(this).click(function () {
			$("#commonSerach").val($(this).text());
			var commonSerach = $("#commonSerach").val();
			var tabletype = $("#zyType").val();
			serachcharecebtlist(tabletype, "common", commonSerach, "", "");
		})
	});
}

/*高级检索*/
$(function () {
	$(".subNav-btn").click(function () {
		$(this).toggleClass("currentDd-btn").siblings(".subNav-btn").removeClass("currentDd-btn");
		$(this).toggleClass("currentDt-btn").siblings(".subNav-btn").removeClass("currentDt-btn");
		// 修改数字控制速度， slideUp(500)控制卷起速度
		$(this).next(".navContent-btn").slideToggle(500).siblings(".navContent-btn").slideUp(500);
	})
})

/*高级检索预加载*/
function iningj(type) {
	var jbmess = "疾病名,ICD,类别,并发症,鉴别诊断,治疗药物,检查,症状";
	var jbfile = "name,DIICD,DISpeciality,DIConcurrent_Disease,DISimilar_Disease,DITreat_Drug,DI_Examination,DIClinical_Symptoms";
	var jcmess = "检查名,类别,疾病,药物";
	var jcfile = "name,EXSpeciality,Disease,Drug";
	var ywmess = "药物名称,药物成分,类别,国家基本药物,适应症,禁忌症,药物相互作用";
	var ywfile = "name,Composition,type,National,adapt,Contraindications,Interactiondrug";
	var zzmess = "症状名,类别,疾病,症状,解剖部位,实验室检查,科室";
	var zzfile = "name,SYSpeciality,SY_ReleDisease,SY_AccompSy,SY_BodyStructure,SY_Examination,SY_Department";
	var ssmess = "手术名,类别,适应症,并发症";
	var ssfile = "name,Speciality,OPIndicationsDisease,OPComplicationDisease";
	var hzxzmess = "题名,类别,疾病,症状,检查,并发症,鉴别诊断,药物";
	var hzxzfile = "PETitle,type,PEDiseases,PE_Symptoms,PE_examination,PE_Complication,PE_SimilarDisease,PE_TreatDrug";
	var blbgmess = "题名,作者,机构,来源,关键字,症状,检查,疾病,并发症,鉴别诊断,药物";
	var blbgfile = "name,CRAuthor,CRUnit,from,keyword,CR_Symptoms,CR_Examination,Disease,CR_Complications,CRSimilar_Disease,CRTreat_Drug";
	var zlznmess = "题名,作者,机构,来源,关键字,疾病,类别";
	var zlznfile = "name,TGAuthor,TGUnit,from,keyword,TG_Diseases,TGSpeciality";
	var allmess = "";

	var mess = "";
	var file = "";
	var zttype = $("input[name='aaaa']:checked").val();
	if (zttype == "jb") {
		file = jbfile;
		mess = jbmess;
	} else if (zttype == "jc") {
		file = jcfile;
		mess = jcmess;
	} else if (zttype == "yw") {
		file = ywfile;
		mess = ywmess;
	} else if (zttype == "zz") {
		file = zzfile;
		mess = zzmess;
	} else if (zttype == "ss") {
		file = ssfile;
		mess = ssmess;
	} else if (zttype == "zlzn") {
		file = zlznfile;
		mess = zlznmess;
	} else if (zttype == "blbg") {
		file = blbgfile;
		mess = blbgmess;
	} else if (zttype == "hzxz") {
		file = hzxzfile;
		mess = hzxzmess;
	}
	var htmls = "<div class='s-condition fL w100'><select>";
	for (var i = 0; i < file.split(",").length; i++) {
		htmls += "<option value='" + file.split(",")[i] + "'>" + mess.split(",")[i] + "</option>";
	}
	htmls += "</select><input style='width:53%;margin:0 15px 0 9px ;'/>";
	htmls += "<select style='width:50px;'><option value='1'>与</option><option value='0'>或</option></select>";
	htmls += "<button onclick='addinput()'><img src='../assets/source/search-gj03.png' /></button> ";
	if (type == 0) {
		htmls += "<button onclick='remove(this)'><img src='../assets/source/search-gj04.png' /></button> ";
	}
	htmls += "</div>";
	$("#serachgaoji").append(htmls);
	gaojiserach();
}
/*高级检索类型切换*/
function clickgjzytype() {

	$(".s-type.fL.w100").find("input").each(function () {
		$(this).click(function () {
			$("#serachgaoji").html("<h3>高级检索</h3>");
			iningj(1);
		});
	});
}
/*高级检索添加input*/
function addinput() {
	iningj(0);
	gaojiserach();
}
/*高级检索删除input*/
function remove(obj) {
	var par = obj.parentNode;
	var _parentElement = par.parentNode;
	if (_parentElement) {
		_parentElement.removeChild(par);
	}
	gaojiserach();
}
/*资源*/
$(function () {
	$(".subNav-zy").click(function () {
		// 修改数字控制速度， slideUp(500)控制卷起速度
		$(this).next(".navContent-zy").slideToggle(500).siblings(".navContent-zy").slideUp(500);
	})
})





/*点击选择按钮值的控制*/
function getzytype(zytype) {
	if (zytype != "") {
		if (zytype == "all") {
			$("#zyType").val(zytype);
			$(".subNav").html("全部");
		} else if (zytype == "yw") {
			$("#zyType").val(zytype);
			$(".subNav").html("药物");
		} else if (zytype == "zz") {
			$("#zyType").val(zytype);
			$(".subNav").html("症状");
		} else if (zytype == "ss") {
			$("#zyType").val(zytype);
			$(".subNav").html("手术");
		} else if (zytype == "jc") {
			$("#zyType").val(zytype);
			$(".subNav").html("检查");
		} else if (zytype == "jb") {
			$("#zyType").val(zytype);
			$(".subNav").html("疾病");
		} else if (zytype == "zlzn") {
			$("#zyType").val(zytype);
			$(".subNav").html("诊疗指南");
		} else if (zytype == "blbg") {
			$("#zyType").val(zytype);
			$(".subNav").html("病例报告");
		} else if (zytype == "hzxz") {
			$("#zyType").val(zytype);
			$(".subNav").html("患者教育");
		}

		// 修改数字控制速度， slideUp(500)控制卷起速度
		$(".subNav").next("div").next(".navContent").slideToggle(500).siblings(".navContent").slideUp(500);
	}
}


/*function hanbao(){
	$(".s-type.fL.w100 a").each(function(){
		$(this).click(function(){
			$(this).parent().find("input").each(function(){
					$(this).removeAttr("checked");
			})
			$(this).find("input").prop("checked",true);
			console.log($(this).html());
		});
	});
}*/
/*热点查询*/
function serachhot() {
	var url = "/front/clickcount/pageList";
	$.ajax({
		type: 'get', //数据发送方式  
		dataType: 'text', //接受数据格式  
		async: false,
		url: url,
		data: {},
		success: function (result) {
			var htmls = "";
			result = JSON.parse(result);;
			if (result.length > 0) {
				for (var s = 0; s < result.length; s++) {
					if (result[s].tabletype == "jb") {
						htmls += "<a href='#' class='blue' zycate='" + result[s].tabletype + "' zyid='" + result[s].documentid + "' >" + result[s].cocumentname + "</a>";
					} else if (result[s].tabletype == "jc") {
						htmls += "<a href='#' class='yellow' zycate='" + result[s].tabletype + "' zyid='" + result[s].documentid + "' >" + result[s].cocumentname + "</a>";
					} else if (result[s].tabletype == "zz") {
						htmls += "<a href='#' class='red' zycate='" + result[s].tabletype + "' zyid='" + result[s].documentid + "' >" + result[s].cocumentname + "</a>";
					} else if (result[s].tabletype == "ss") {
						htmls += "<a href='#' class='blue' zycate='" + result[s].tabletype + "' zyid='" + result[s].documentid + "' >" + result[s].cocumentname + "</a>";
					} else if (result[s].tabletype == "zcy") {
						htmls += "<a href='#' class='green' zycate='" + result[s].tabletype + "' zyid='" + result[s].documentid + "' >" + result[s].cocumentname + "</a>";
					} else if (result[s].tabletype == "hxyw") {
						htmls += "<a href='#' class='fivecolor' zycate='" + result[s].tabletype + "' zyid='" + result[s].documentid + "' >" + result[s].cocumentname + "</a>";
					}

				}
			}
			$("#tagbox").html(htmls);
		}
	});
	serachhotOne();
}

/*点击查看详情*/
function serachhotOne() {
	$("#tagbox a").each(function () {
		$(this).click(function () {
			var b = islogin();
			if (b) { return }
			var tabletype = $(this).attr("zycate");
			var id = $(this).attr("zyid");
			var newWin = window.open();
			newWin.location.href = "/view/disease/todetail?id=" + id + "&tabletype=" + tabletype;
		});
	});
}
//查询最近
function serachrecent() {
	var url = "/front/date/listBytime";
	$.ajax({
		type: 'get', //数据发送方式  
		dataType: 'text', //接受数据格式  
		async: false,
		url: url,
		data: {},
		success: function (result) {
			result = JSON.parse(result);;
			if (result.length > 0) {
				for (var s = 0; s < result.length; s++) {
					if (s == 9 || s == 19 || s == 29 || s == 39) {
						if (result[s].ZYCATE == "jb") {
							var htmls = "<li><a href='#' zycate='" + result[s].ZYCATE + "' zyid='" + result[s].DIID + "' >" + result[s].DICHINESENAME + "</a><span></span></li>";
							$("#jbrecent").append(htmls);
						} else if (result[s].ZYCATE == "jc") {
							var htmls = "<li><a href='#' zycate='" + result[s].ZYCATE + "' zyid='" + result[s].EXID + "' >" + result[s].EXCHINESENAME + "</a><span></span></li>";
							$("#jcrecent").append(htmls);
						} else if (result[s].ZYCATE == "zz") {
							var htmls = "<li><a href='#' zycate='" + result[s].ZYCATE + "' zyid='" + result[s].SYID + "' >" + result[s].SYCHINESENAME + "</a><span></span></li>";
							$("#zzrecent").append(htmls);
						} else if (result[s].ZYCATE == "ss") {
							var htmls = "<li><a href='#' zycate='" + result[s].ZYCATE + "' zyid='" + result[s].OPID + "' >" + result[s].OPCHINESENAME + "</a><span></span></li>";
							$("#ssrecent").append(htmls);
						} else if (result[s].ZYCATE == "zcy" || result[s].ZYCATE == "hxyw") {
							var htmls = "<li><a href='#' zycate='" + result[s].ZYCATE + "' zyid='" + result[s].ID + "' >" + result[s].CHINESENAME + "</a><span></span></li>";
							$("#ywrecent").append(htmls);
						}
					} else {
						if (result[s].ZYCATE == "jb") {
							var htmls = "<li><a href='#' zycate='" + result[s].ZYCATE + "' zyid='" + result[s].DIID + "' >" + result[s].DICHINESENAME + "</a><span> | </span></li>";
							$("#jbrecent").append(htmls);
						} else if (result[s].ZYCATE == "jc") {
							var htmls = "<li><a href='#' zycate='" + result[s].ZYCATE + "' zyid='" + result[s].EXID + "' >" + result[s].EXCHINESENAME + "</a><span> | </span></li>";
							$("#jcrecent").append(htmls);
						} else if (result[s].ZYCATE == "zz") {
							var htmls = "<li><a href='#' zycate='" + result[s].ZYCATE + "' zyid='" + result[s].SYID + "' >" + result[s].SYCHINESENAME + "</a><span> | </span></li>";
							$("#zzrecent").append(htmls);
						} else if (result[s].ZYCATE == "ss") {
							var htmls = "<li><a href='#' zycate='" + result[s].ZYCATE + "' zyid='" + result[s].OPID + "' >" + result[s].OPCHINESENAME + "</a><span> | </span></li>";
							$("#ssrecent").append(htmls);
						} else if (result[s].ZYCATE == "zcy" || result[s].ZYCATE == "hxyw") {
							var htmls = "<li><a href='#' zycate='" + result[s].ZYCATE + "' zyid='" + result[s].ID + "' >" + result[s].CHINESENAME + "</a><span> | </span></li>";
							$("#ywrecent").append(htmls);
						}
					}

				}
			}
		}
	});
	serachrecentOne();
}

/*点击查看详情*/
function serachrecentOne() {
	$(".new-classify.fL.w100").find("li").each(function () {
		$(this).find("a").click(function () {
			var b = islogin();
			if (b) { return }
			var tabletype = $(this).attr("zycate");
			var id = $(this).attr("zyid");
			var newWin = window.open();
			newWin.location.href = "/view/disease/todetail?id=" + id + "&tabletype=" + tabletype;
		});
	});
}
/* 点击查询列表,跳转页面 */
function serachcharecebtlist(tabletype, serachtype, commonSerach, againSerach, expertSerach) {
	var b = islogin();
	if (b) { return }
	url = "/view/disease/tolist?tabletype=" + tabletype + "&serachtype=" + serachtype + "&commonSerach=" + commonSerach + "&againSerach=" + againSerach + "&expertSerach=" + encodeURIComponent(expertSerach);
	window.location.href = url;
}

function serachcommon() {
	$(".rg-button.fL").find("button").click(function () {
		var commonSerach = $("#commonSerach").val();
		if (commonSerach == "" || commonSerach == "%" || commonSerach == "*") {
			layer.msg('请输入药物、疾病、检查、症状等');
			return;
		}
		var tabletype = $("#zyType").val();
		serachcharecebtlist(tabletype, "common", commonSerach, "", "");
	});

	$("#commonSerach").keydown(function (event) {
		if (event.keyCode == 13) {
			var commonSerach = $("#commonSerach").val();
			if (commonSerach == "") {
				layer.msg('请输入药物、疾病、检查、症状等');
				return;
			}
			var tabletype = $("#zyType").val();
			serachcharecebtlist(tabletype, "common", commonSerach, "", "");
		}

	});

}

function gaojiserach() {

	$(".btm-center").find("button").click(function () {
		var tabletype = $("input[name='aaaa']:checked").val();
		var expertSerach = {};
		var length = $(".s-condition.fL.w100").length;
		$(".s-condition.fL.w100").each(function (i, dom) {

			var file = $(this).children("select:first-child").val();
			var values = $(this).find("input").val();
			var filetype = $(this).children('select').eq(1).val();
			if (values != "") {
				if (expertSerach.hasOwnProperty(file)) {
					if (i == length - 1) {
						expertSerach[file] = expertSerach[file] + ";" + values + "-end ";
					} else {
						if (filetype == 1) {
							expertSerach[file] = expertSerach[file] + ";" + values + "-and ";
						} else {
							expertSerach[file] = expertSerach[file] + ";" + values + "-or ";
						}
					}

				} else {
					if (i == length - 1) {
						expertSerach[file] = values + "-" + "end ";
					} else {
						if (filetype == 1) {
							expertSerach[file] = values + "-" + "and ";
						} else {
							expertSerach[file] = values + "-" + " or ";
						}
					}
				}

			}
		});
		if (JSON.stringify(expertSerach) != "{}") {
			serachcharecebtlist(tabletype, "expert", "", "", JSON.stringify(expertSerach));
		} else {
			layer.msg('请输入关键字');
			return;
		}

	});
	$("#serachgaoji").find("input").keydown(function () {
		if (event.keyCode == 13) {
			var tabletype = $("input[name='aaaa']:checked").val();
			var expertSerach = {};
			var length = $(".s-condition.fL.w100").length;
			$(".s-condition.fL.w100").each(function (i, dom) {

				var file = $(this).children("select:first-child").val();
				var values = $(this).find("input").val();
				var filetype = $(this).children('select').eq(1).val();
				if (values != "") {
					if (expertSerach.hasOwnProperty(file)) {
						if (i == length - 1) {
							expertSerach[file] = expertSerach[file] + ";" + values + "-end ";
						} else {
							if (filetype == 1) {
								expertSerach[file] = expertSerach[file] + ";" + values + "-and ";
							} else {
								expertSerach[file] = expertSerach[file] + ";" + values + "-or ";
							}
						}

					} else {
						if (i == length - 1) {
							expertSerach[file] = values + "-" + "end ";
						} else {
							if (filetype == 1) {
								expertSerach[file] = values + "-" + "and ";
							} else {
								expertSerach[file] = values + "-" + " or ";
							}
						}
					}

				}
			});
			if (JSON.stringify(expertSerach) != "{}") {
				serachcharecebtlist(tabletype, "expert", "", "", JSON.stringify(expertSerach));
			} else {
				layer.msg('请输入关键字');
				return;
			}
			//			serachcharecebtlist(tabletype,"expert","","",JSON.stringify(expertSerach));
		}
	});
}






