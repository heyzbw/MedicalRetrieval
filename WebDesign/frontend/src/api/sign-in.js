$(function () {
	$.fn.extend({
		toggSpecify: function (winBox, closeBar) {
			//this 被点击显示winBox
			//closeBar被点击则关闭 winBox,默认 closeBar=#mb
			var winBoxBg = '<div id=\"mb\"></div>'
			$(this).click(function () {
				if (!$("#mb")) {
					$("template").append(winBoxBg);
				}
				$("#mb," + winBox).show();
			});
			closeBar ? closeBar : closeBar = "#mb";
			$(closeBar).click(function () {
				$("#mb," + winBox).hide();
			});
		},

	});
	$(".btn2").toggSpecify(".showbox2", ".close,#mb");
})


