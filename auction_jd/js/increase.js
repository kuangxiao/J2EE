//拍卖状态
var auctionStatus;
//拍卖编号
var paimaiId;
//SKU
var sku;
//剩余时间
var remainTime;
//查询起始
var queryStart=0;
//查询结束
var queryEnd=9;
//倒计时开关
var countDown=0;
//封顶价
var maxPrice=0;
//当前价
var currentPrice=1;
//起拍价
var startPrice=1;
//最低加价幅度
var priceLowerOffset=1;
//最高加价幅度
var priceHigherOffset=1000;
//推荐列表
var recommendIdList="";

/**
 * 正在拍卖：点+
 * */
if(!String.prototype.trim) {
     String.prototype.trim = function () {
         return this.replace(/^\s+|\s+$/g,'');
    };
}
function incre(){
	var userprice = $("#bidPrice").val();
	var price = Number(jQuery.trim(userprice));
	var limitPrice = !isNaN(maxPrice) && maxPrice >= 1;
	
	if(limitPrice){
		if(price+1>maxPrice){
			$("#bidPrice").val(maxPrice);
			return ;
		}
	}
	if(price+1<currentPrice+priceLowerOffset){
		$("#bidPrice").val(currentPrice+priceLowerOffset);
	}
	else if(price+1>=currentPrice+priceLowerOffset && price+1<=currentPrice+priceHigherOffset){
		$("#bidPrice").val(price+1);
	}
	else{
		$("#bidPrice").val(currentPrice+priceLowerOffset);
	}
}


/**
 * 正在拍卖：点-
 * */
function decre(){
	var userprice = $("#bidPrice").val();
	var price = Number(jQuery.trim(userprice));
	var limitPrice = !isNaN(maxPrice) && maxPrice >= 1;
	if(limitPrice){
		if(price-1>maxPrice){
			$("#bidPrice").val(maxPrice);
			return ;
		}
	}
	if(price-1<currentPrice+priceLowerOffset){
		$("#bidPrice").val(currentPrice+priceLowerOffset);
	}
	else if(price-1>=currentPrice+priceLowerOffset && price-1<=currentPrice+priceHigherOffset){
		$("#bidPrice").val(price-1);
	}
	else{
		$("#bidPrice").val(currentPrice+priceLowerOffset);
	}
}



/**
 * 出价
 * */
function bid(){
	var userprice = $("#bidPrice").val();
	var price = Number(jQuery.trim(userprice));
	if(checkPrice(price)){
		var url = "/services/bid.action?t=" + getRamdomNumber();
	    var data = {paimaiId:paimaiId,price:price,proxyFlag:0,bidSource:0};
	    jQuery.getJSON(url,data,function(jqXHR){
			if(jqXHR!=undefined){
				if(jqXHR.result=='200'){
					dialogSuccess("恭喜您，出价成功",5,"秒钟后窗口将会自动关闭");
				}else if(jqXHR.result=='login'){
					window.location.href='//passport.jd.com/new/login.aspx?ReturnUrl='+window.location.href;
				}else if(jqXHR.result=='600'){
					dialogError("很抱歉，出价失败",5,"当日已流拍两次，不能继续出价");
				}else if(jqXHR.result=='573'){
					dialogError("啊哦，出价失败",5,"欢迎再次参与~");
				}else{
					dialogError("很抱歉，出价失败",5,jqXHR.message);
				}
				
				pageCurrentReload();
			}
	    });
	}else{
		//验证失败,给一个默认值
		$("#bidPrice").val(Number(Number(currentPrice)+Number(priceLowerOffset)));
	}
}

$(function(){
	//回车出价
	$("#bidPrice").keydown(function(event){
	    if(event.which == 13)       //13等于回车键(Enter)键值,ctrlKey 等于 Ctrl
	    {
		  bid();
	    }
	});
});


/**
 * 出价后页面重新异步加载
 * */
function pageCurrentReload(){
	queryStart=0;
	queryEnd=9;
	initCurrentData();
}
function jdThickBoxclose(){
	$(".tip-box warn-box").hide();
}

/**
 * 出价前价格校验
 * */
function checkPrice(price){
	if(!/^-?\d+$/.test(price)){
        dialogError("很抱歉，出价失败",5,"出价必须为正整数");
        return false;
    }
	if(maxPrice>0 && price>maxPrice){
		dialogError("很抱歉，出价失败",5,"出价不能超过本次竞拍封顶价（￥"+maxPrice+"）");
		return false;
	}
	if(price<=currentPrice){
		dialogError("很抱歉，出价失败",5,"出价不能低于当前价（￥"+currentPrice+"）");
		return false;
	}
	if(price<currentPrice+priceLowerOffset){
		dialogError("很抱歉，出价失败",5,"加价幅度不能低于最低加价幅度（￥"+priceLowerOffset+"）");
		return false;
	}
	if(price>currentPrice+priceHigherOffset){
		dialogError("很抱歉，出价失败",5,"加价幅度不能高于最高加价幅度（￥"+priceHigherOffset+"）");
		return false;
	}
	return true;
}
function doErrorMsg(text1,text2){
	alert(text1+","+text2);
}
function loadBidList(){
	queryEnd=Number(queryEnd)+5;
	if(queryEnd>30){
		queryEnd=30;
	}
	initCurrentData();
}

setInterval(remainTimeCountDown,1000);

function remainTimeCountDown(){
	if(countDown==1 && remainTime>0) {
		remainTime = remainTime - 1000;
		if (remainTime<=0) {
			initCurrentData();
		}
		var timeText = timeBetweenText();
		$("#auction1Timer").html(timeText);
		$("#auction3Timer").html(timeText);
	}
}

function rightZeroStr(v) {
    if (v < 10) {
        return "0" + v;
    }
    return v + "";
}

function timeBetweenText(){
	var dayOfMil = (24 * 60 * 60 * 1000);
    var hourOfMil = 60 * 60 * 1000;
    var minOfMil = 60 * 1000;
    var secOfMil = 1000;
    
    var hourOffset = remainTime % dayOfMil;
    var minuteOffset = hourOffset % hourOfMil;
    var seccondOffset = minuteOffset % minOfMil;
    
    var hours = Math.floor(remainTime / hourOfMil);
    var minutes = Math.floor(minuteOffset / minOfMil);
    var seconds = Math.floor(seccondOffset / secOfMil);

    if(hours>0){
    	return "<em class=\"hour\">"+rightZeroStr(hours)+"</em>时<em class=\"hour\">"+rightZeroStr(minutes)+"</em>分<em class=\"hour\">"+rightZeroStr(seconds)+"</em>秒";
    }else if(minutes>0){
    	return "<em class=\"hour\">"+rightZeroStr(minutes)+"</em>分<em class=\"hour\">"+rightZeroStr(seconds)+"</em>秒";
    }else if (seconds>0) {
		return "<em class=\"hour\">"+rightZeroStr(seconds)+"</em>秒";
	}else{
		return "<em class=\"hour\">0</em>秒";
	}
    
}


Date.prototype.format = function(format){ 
	var o = { 
	"M+" : this.getMonth()+1, //month 
	"d+" : this.getDate(), //day 
	"h+" : this.getHours(), //hour 
	"m+" : this.getMinutes(), //minute 
	"s+" : this.getSeconds(), //second 
	"q+" : Math.floor((this.getMonth()+3)/3), //quarter 
	"S" : this.getMilliseconds() //millisecond 
	}
}
function loadJdPrice(){
	
	var showJdPrice = $("#showJdPrice").val();
	if(Number(showJdPrice)<1){
		return ;
	}
	
	var url="/json/current/queryJdPrice?sku="+sku+"&paimaiId="+paimaiId;
	jQuery.getJSON(url,function (response) {
		var jdPrice = response.jdPrice;
		var access = response.access;
		if (jdPrice>1) {
			var jdPriceInfo = "<em class=\"line\"></em>京东价：<del>&yen;"+jdPrice+"</del>";
			$("#auction1dangqianjia").after(jdPriceInfo);
			$("#auction2dangqianjia").after(jdPriceInfo);
			$("#auction3dangqianjia").after(jdPriceInfo);
		}
		if (access>0) {
			var accessInfo = "围观数：<span class=\"color33 font14\">"+access+"次</span>";
			$("#auction1weiguan").html(accessInfo);
			$("#auction2weiguan").html(accessInfo);
			$("#auction3weiguan").html(accessInfo);
		}
	});
}

function loadCurrentPriceList(){
	var url = "/json/current/queryList.action?paimaiIds="+recommendIdList;
	jQuery.getJSON(url,function (response) {
		$(response).each(function(key,value){
			try{
				var id=value.paimaiId;
				var price=value.currentPriceStr;
				$("#sameSku_price_"+id).html("<i>¥</i>"+price);
				$("#recomm_price_"+id).html("<i>¥</i>"+price);
				$("#recomm_narrow_price_"+id).html("<i>¥</i>"+price);
			}catch(error){
				console.log(error);
			}
		});
	});
}

function loadPaimaiBigField(){
	
	var showIntro = $("#showIntro").val();
	if(Number(showIntro)<1){
		return ;
	}
	
	var cateId = $("#firstCateId").val();
	var url = "/json/paimaiProduct/queryPaimaiBigField?id="+sku+"&cateId="+cateId;
	jQuery.getJSON(url,function (response) {
		var intro = response.model.productIntroduction;
		var speci = response.model.productSpecification;
		
		$("#productIntroduction").html(intro);
		$("#productSpecifacation").html(speci);
		
		var effectNum = 0;
		var sameSkuInfo = "";
		$(response.model.sameSku).each(function(key,value){
			var id = value.paimaiId;
			var productImage = value.productImage;
			var waiguan = value.appearance;
			var baozhuang = value.packaging;
			var useStatus = value.useStatus;
			var auctionType = value.auctionType;
			if(id!=paimaiId){
				effectNum++;
				if(auctionType==-1){
					var info ="<a href=\"//dbditem.jd.com/"+id+"-1\" target=\"_blank\"><div class=\"same_item clearfix\"><span class=\"price fr\"><i>¥</i>"+value.onePrice+"</span><span class=\"useIcon ui"+useStatus+"\"></span><p>"+baozhuang+"，"+waiguan+"</p></div></a>";
				}else{
					var info ="<a href=\"//dbditem.jd.com/"+id+"-1\" target=\"_blank\"><div class=\"same_item clearfix\"><span class=\"price fr\" id='sameSku_price_"+id+"'><i></i></span><span class=\"useIcon ui"+useStatus+"\"></span><p>"+baozhuang+"，"+waiguan+"</p></div></a>";
					if(recommendIdList==null ||recommendIdList==""||recommendIdList===undefined){
						recommendIdList += id;
					}else{
						recommendIdList += "-"+id;
					}
				}
				sameSkuInfo += info;
			}
			
		});
		if(effectNum>0){
			$("#sameSku").html(sameSkuInfo);
			$("#sameSkuList").attr("class","details_column wide column_padding");
		}
		
		effectNum = 0;
		var recommendInfo = "";
		var recommendInfoNarrow = "";
		$(response.model.recomList).each(function(key,value){
			var id = value.paimaiId;
			var productImage = value.productImage;
			var useStatus = value.useStatus;
			var title = value.title;
			var auctionType = value.auctionType;
			var infoNarrow = "";
			var info = "";
			if(id!=paimaiId){
				effectNum++;
				if(auctionType==-1){
					infoNarrow="<li><a href=\"//dbditem.jd.com/"+id+"-2\" target=\"_blank\" class=\"pic\"><img width=\"100\" height=\"100\" class=\"err-product\" src=\"//img10.360buyimg.com/n2/"+productImage+"\" data-lazyload=\"//img10.360buyimg.com/n2/"+productImage+"\"/></a><a href=\"//dbditem.jd.com/"+id+"-2\" target=\"_blank\" class=\"name\">"+title+"</a><span class=\"price\"><i>￥</i>"+value.onePrice+"</span><span class=\"useIcon ui"+useStatus+"\"></span></li>";
					info="<li><a href=\"//dbditem.jd.com/"+id+"-2\" target=\"_blank\" class=\"pic\"><img width=\"100\" height=\"100\" class=\"err-product\" src=\"//img10.360buyimg.com/n2/"+productImage+"\" data-lazyload=\"//img10.360buyimg.com/n2/"+productImage+"\"/></a><a href=\"//dbditem.jd.com/"+id+"-2\" target=\"_blank\" class=\"name\">"+title+"</a><span class=\"price\"><i>￥</i>"+value.onePrice+"</span><span class=\"useIcon ui"+useStatus+"\"></span></li>";
				}else{
					infoNarrow="<li><a href=\"//dbditem.jd.com/"+id+"-2\" target=\"_blank\" class=\"pic\"><img width=\"100\" height=\"100\" class=\"err-product\" src=\"//img10.360buyimg.com/n2/"+productImage+"\" data-lazyload=\"//img10.360buyimg.com/n2/"+productImage+"\"/></a><a href=\"//dbditem.jd.com/"+id+"-2\" target=\"_blank\" class=\"name\">"+title+"</a><span class=\"price\" id=\"recomm_narrow_price_"+id+"\"><i></i></span><span class=\"useIcon ui"+useStatus+"\"></span></li>";
					info="<li><a href=\"//dbditem.jd.com/"+id+"-2\" target=\"_blank\" class=\"pic\"><img width=\"100\" height=\"100\" class=\"err-product\" src=\"//img10.360buyimg.com/n2/"+productImage+"\" data-lazyload=\"//img10.360buyimg.com/n2/"+productImage+"\"/></a><a href=\"//dbditem.jd.com/"+id+"-2\" target=\"_blank\" class=\"name\">"+title+"</a><span class=\"price\" id=\"recomm_price_"+id+"\"><i></i></span><span class=\"useIcon ui"+useStatus+"\"></span></li>";
					if(recommendIdList==null ||recommendIdList==""||recommendIdList===undefined){
						recommendIdList += id;
					}else{
						recommendIdList += "-"+id;
					}
				}
				recommendInfo += info;
				recommendInfoNarrow += infoNarrow;
			}
		});
		
		if(effectNum>0){
			$("#recommendUL").html(recommendInfo);
			$("#recommendList").attr("class","details_column wide");
			$("#recommendULNarrow").html(recommendInfoNarrow);
			$("#recommendListNarrow").attr("class","details_column narrow");
		}
		
		if(recommendIdList!=""){
			loadCurrentPriceList();
		}
		
	});
}

$(function(){
	paimaiId = $("#paimaiId").val();
	sku = $("#productId").val();
	initCurrentData();
	if(sku>0){
		loadPaimaiBigField();
		loadJdPrice();
	}else{
		console.log("sku异常，不会获取大字段数据");
	}
});
function getRamdomNumber(){
	var num=""; 
	for(var i=0;i<6;i++) 
	{ 
		num+=Math.floor(Math.random()*10); 
	} 
	return num;
}
function initCurrentData(){
	var paimaiId=$("#paimaiId").val();
	var url = "/json/current/englishquery?paimaiId="+paimaiId+"&skuId=0&t="+getRamdomNumber()+"&start="+queryStart+"&end="+queryEnd;
	$.getJSON(url,function (response) {
		responseInstall(response);
		countDown=1;
	});	
}
function responseInstall(response){
	auctionStatus = response.auctionStatus;
	remainTime = response.remainTime;
	currentPrice = Number(response.currentPrice);
	priceLowerOffset = Number($("#lowPriceOffSet").val().trim());
	priceHigherOffset = Number($("#highPriceOffSet").val().trim());
	
	if (auctionStatus==0) {
		$("#auctionStatus1").attr("class","auction3 hide");
		$("#auctionStatus2").attr("class","auction2 hide");
		$("#auctionStatus0").attr("class","auction1");
	}
	
	if ((auctionStatus==0&&remainTime<0 || auctionStatus==1 && remainTime>0)) {
		$("#auctionStatus2").attr("class","auction2 hide");
		$("#auctionStatus0").attr("class","auction1 hide");
		$("#auctionStatus1").attr("class","auction3");
	}
	
	if ((auctionStatus==2)||(auctionStatus==1 && remainTime<0) ||(auctionStatus==4)) {
		$("#auctionStatus2").attr("class","auction2");
		$("#auctionStatus0").attr("class","auction1 hide");
		$("#auctionStatus1").attr("class","auction3 hide");
//		if(response.currentUser!=null && $("#userinfo").val()!=null && $("#userinfo").val()!="" && $("#userinfo").val()==response.currentUser){
//			//dialogJump("恭喜，成功获拍！",5,"秒钟后将自动跳转结算页...","//pm.jd.com/pm/"+paimaiId);
//			dialogJump("恭喜，成功获拍！",5,"秒钟后将自动跳转结算页...</br> 请在1小时内转订单，否则自动取消获拍资格，并扣除本次参拍的保证金或京豆","//pm.jd.com/pm/"+paimaiId);
//		}
		if(response.orderStatus==1){
//			//dialogJump("恭喜，成功获拍！",5,"秒钟后将自动跳转结算页...","//pm.jd.com/pm/"+paimaiId);
			dialogJump("恭喜，成功获拍！",5,"秒钟后将自动跳转结算页...</br> 请在1小时内转订单，否则自动取消获拍资格，并扣除本次参拍的保证金或京豆","//dbpm.jd.com/pm/"+paimaiId);
		}
	}
	
	var currentPriceInfo = "<em class=\"font12\">&yen;</em>"+response.currentPrice;
	$("#auction1dangqianjia").html(currentPriceInfo);
	$("#auction2dangqianjia").html(currentPriceInfo);
	$("#auction3dangqianjia").html(currentPriceInfo);
	if(Number(response.bidCount)>0){
		$("#bidCount").html("出价记录（共<em class=\"num\">"+response.bidCount+"</em>次出价）");
		$("#bidCountNarrow").html("出价记录（共<em class=\"num\">"+response.bidCount+"</em>次出价）");
	}else{
		$("#bidCount").html("出价记录");
		$("#bidCountNarrow").html("出价记录");
	}
	var bidInfo = "";
	var bidInfoNarrow = "";
	$(response.bidList).each(function(key,value){
		var bidTime = value.bidTimeStr1;
		var pin = value.username;
		var price = value.priceStr;
		var status = "无效";
		if(key==0){
			status="领先";
			var bidRecord = "<dd class=\"clearfix\"><span class=\"wd1\">"+bidTime+"</span><span class=\"wd2\"><i class=\"phone\">"+pin+"</i><div class=\"line\"></div><i class=\"price\">￥"+price+"</i></span><span class=\"wd3\"><i>"+status+"</i></span></dd>";
			var bidRecordNarrow = "<dd><span class=\"sp1\">"+(key+1)+"</span><span class=\"sp2\">"+bidTime+"</span><span class=\"sp3\">"+pin+"</span><span class=\"sp4\"><b>&yen;</b>"+price+"</span><span class=\"sp5\"><i>领先</i></span></dd>";
			bidInfo += bidRecord;
			bidInfoNarrow += bidRecordNarrow;
		}else{
			status="出局";
			var bidRecord = "<dd class=\"clearfix\"><span class=\"wd1\">"+bidTime+"</span><span class=\"wd2\"><i class=\"phone\">"+pin+"</i><div class=\"line\"></div><i class=\"price\">￥"+price+"</i></span><span class=\"wd3 out\"><i>"+status+"</i></span></dd>";
			var bidRecordNarrow = "<dd><span class=\"sp1\">"+(key+1)+"</span><span class=\"sp2\">"+bidTime+"</span><span class=\"sp3\">"+pin+"</span><span class=\"sp4\"><b>&yen;</b>"+price+"</span><span class=\"sp5 out\"><i>出局</i></span></dd>";
			bidInfo += bidRecord;
			bidInfoNarrow += bidRecordNarrow;
		}
		
	});
	$(".records dl dt").nextAll().remove();
	$(".records dl dt").after(bidInfo);
	$("#records-narrow dl dt").nextAll().remove();
	$("#records-narrow dl dt").after(bidInfoNarrow);
}
