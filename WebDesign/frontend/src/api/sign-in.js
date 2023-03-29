import $ from 'jquery'
$(function () {
	$.fn.extend({
		toggleWin: function (winBox, boolean) {
			//this 点击显示winBox 点击除了this的 其他地方则隐藏winBox
			//boolean赋值 且 == true 点击除了this和winBox 的 其他地方则隐藏winBox
			$(this).click(function (ev) {
				$(winBox).show();
				ev.stopPropagation();
			});
			$(document).click(function (e) {
				$(winBox).hide();
			});
			if (boolean && boolean == true) {
				$(winBox).click(function (ev) {
					ev.stopPropagation();
				});
			}
		},
		toggSpecify: function (winBox, closeBar) {
			//this 被点击显示winBox
			//closeBar被点击则关闭 winBox,默认 closeBar=#mb
			var winBoxBg = '<div id=\"mb\"></div>'
			$(this).click(function () {
				if (!$("#mb")) {
					$("body").append(winBoxBg);
				}
				$("#mb," + winBox).show();
			});
			closeBar ? closeBar : closeBar = "#mb";
			$(closeBar).click(function () {
				$("#mb," + winBox).hide();
			});
		},

	});

	$(".btn1").toggSpecify(".showbox1", ".close,#mb");
	$(".btn2").toggSpecify(".showbox2", ".close,#mb");
	$(".btn3").toggSpecify(".showbox3", ".close,#mb");
	$(".btn4").toggSpecify(".showbox4", ".close,#mb");
	$(".btn5").toggSpecify(".showbox5", ".close,#mb");
})


