var xinm = new Array();
xinm[0] = "白金香"
xinm[1] = "白应梅"
xinm[2] = "柏仁燕"
xinm[3] = "包颜琳"
xinm[4] = "鲍学梅"
xinm[5] = "鲍颖"
xinm[6] = "素材"
xinm[7] = "蔡艳"
xinm[8] = "蔡玉"
xinm[9] = "曹发敏"
xinm[10] = "白金香1"
xinm[11] = "白应梅1"
xinm[12] = "柏仁燕1"
xinm[13] = "包颜琳1"
xinm[14] = "鲍学梅1"
xinm[15] = "鲍颖1"
xinm[16] = "素材1"
xinm[17] = "蔡艳1"
xinm[18] = "蔡玉1"
xinm[19] = "曹发敏1"


var phone = new Array();
phone[0] = "13100138001"
phone[1] = "13200138002"
phone[2] = "13300138003"
phone[3] = "13400138004"
phone[4] = "13500138005"
phone[5] = "13600138006"
phone[6] = "13700138007"
phone[7] = "13800138008"
phone[8] = "13900138009"
phone[9] = "13110138010"
phone[10] = "12100138001"
phone[11] = "12200138002"
phone[12] = "12300138003"
phone[13] = "12400138004"
phone[14] = "12500138005"
phone[15] = "12600138006"
phone[16] = "12700138007"
phone[17] = "12800138008"
phone[18] = "12900138009"
phone[19] = "12110138010"

var nametxt = $('.name');
var phonetxt = $('.phone');
var pcount = xinm.length-1;//参加人数
var runing = true;
var td = 19;//中奖,从最小奖开始，共10个名额
var num = 0;
var t;
//开始停止
function start() {
	if (runing) {
		runing = false;
		$('#btntxt').removeClass('start').addClass('stop');
        $('.zjma').removeClass('bounceInDown').addClass('zoomOut ');
		$('#btntxt').html('停止');
		startNum()
	} else {
		runing = true;
		$('#btntxt').html('开始');
		stop();
		zd();//
	}
}
//循环参加名单
function startNum() {
	num = Math.floor(Math.random() * pcount);
	nametxt.html(xinm[num]);
	phonetxt.html(phone[num]);
	t = setTimeout(startNum, 0);
}
//停止跳动
function stop() {
	pcount = xinm.length-1;
	clearInterval(t);
	t = 0;
}
//从一等奖开始指定前3名
function zd() {
    if(td > 0){
		//打印中奖者名单
        $('.zjmd').show();
        $('.zjma').hide();
		$('.list').prepend("<span style='display: block;margin-left: 3px;width:90px;float: left'>"+
            "<span style='display: block;float: left;color:#fff;width:30px;text-align: right;font-weight: 700'>"+td+"."+"</span>"/*+' '*/+
            "<span style='display: block;float: left;color:#FF8900;width:60px;text-align:left'>"+xinm[num]+"</span>"/*+
            "<span style='display: block;float: left;color:#FF8900;width:100px;text-align:center'>"+phone[num]+"</span>"+
            "</span>"*/);
		if(pcount <= 0){
            prompt("投票结束 !");
		}
		//将已中奖者从数组中"删除",防止二次中奖
		xinm.splice($.inArray(xinm[num], xinm), 1);
		//phone.splice($.inArray(phone[num], phone), 1);
	}
    else{
    if(pcount <= 0){
        prompt("投票结束 !");
    }
}
	td = td - 1;
}
