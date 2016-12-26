function Lottery() {
    var me = this;
    var isReady = true;
    var looping = false;
    var showing = false;

    this.process = {};
    this.person = {};
    var personNum = 0;
    var arrPhone = [];

    //循环timer
    var t = 0;

    this.getCnLevel = function (level) {
        switch (level) {
            case 0:
                return "特等奖";
            case 1:
                return "一等奖";
            case 2:
                return "二等奖";
            case 3:
                return "三等奖";
            case 4:
                return "四等奖";
            default:
                return "？？？";
        }
    };


    this.start = function () {
        if (isReady) {
            if (!looping) {
                me.loadProcess(loopShow());
            }
            else {
                stopLoop();
            }
        }
    };

    me.readyLottery = function () {
        if (!showing && !isReady){
            isReady = true;
            me.loadProcess(function () {
                $("#showPhone").html("12345678910");
                $("#showName").html("天逸集团");
                $('#lucky').html("");
            });
        }

    };

    var showLevel = function (process) {
        if (process) {
            $("#level").html(me.getCnLevel(process.level) + "中奖者名单：");
        }
        else {
            $("#level").html("特等奖中奖者名单：");
        }
    };
    me.loadProcess = function (fn) {
        $.get("process", function (data) {
            me.process = data;
            var titile = $("#titile");
            if ($.isEmptyObject(data) || data.sort == 0) {
                titile.html("抽奖完成");
                showLevel();
                me.start = function () {
                };
            }
            else {
                showLevel(data);
                titile.html(me.getCnLevel(data.level));
                if (fn) {
                    fn();
                }
            }

        });
    };

    function loopShow() {
        function start() {
            var num = Math.floor(Math.random() * (personNum - 1));
            var phone = arrPhone[num];
            var name = me.person[phone];
            $("#showPhone").html(phone);
            $("#showName").html(name);
        }

        looping = true;
        t = setInterval(start, 1);
    }

    function stopLoop() {
        looping = false;
        clearInterval(t);
        t = 0;
        lottery();
    }

    function lottery() {
        $.get("lottery", {}, function (data) {
            if (!$.isEmptyObject(data)) {
                printLuckyPerson(data);
            }
        })
    }

    function printLuckyPerson(person) {
        isReady = false;
        showing = true;
        $('.zjmd').show();
        $('.zjma').hide();
        var phone = Object.keys(person);
        var size = phone.length;
        var i = 0;
        $('#lucky').html("");
        function addLucky() {
            var tPhone = phone[i++];
            $('#lucky').append("<span style='display: block;margin-left: 3px;width:90px;float: left'>" +
                "<span style='display: block;float: left;color:#FF8900;width:60px;text-align:left'>" + person[tPhone] + "</span>");
            $("#showPhone").html(tPhone);
            $("#showName").html(person[tPhone]);

            if (i >= size) {
                clearInterval(t);
                t = 0;
                showing = false;
            }
        }

        t = setInterval(addLucky, 100);
    }

    function loadPerson() {
        $.get("person", {}, function (data) {
            me.person = data;
            arrPhone = Object.keys(data);
            personNum = arrPhone.length;
        })
    }

    loadPerson();
}