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

    /**
     * 根据定义的流程,显示中文含义
     * @param level
     * @returns {*}
     */
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
            case '0':
                return "特等奖";
            case '1':
                return "一等奖";
            case '2':
                return "二等奖";
            case '3':
                return "三等奖";
            case '4':
                return "四等奖";
            default:
                return "？？？";
        }
    };

    /**
     * 开始抽奖
     * 在调用一次以后，需要先调用ready重置状态后，方可正常调用
     */
    this.start = function () {
        if (isReady) {
            if (!looping) {
                me.loadProcess(loopShow());
            }
            else {
                lottery(stopLoop);
            }
        }
    };

    /**
     * 初始化信息,准备抽奖
     * 防止多次触发抽奖操作
     */
    me.ready = function () {
        if (!showing && !isReady) {
            isReady = true;
            me.loadProcess(function () {
                $("#showPhone").html("12345678910");
                $("#showName").html("天逸集团");
                $('#lucky').html("");
            });
        }

    };

    /**
     * 载入当前抽奖流程
     * @param fn
     */
    me.loadProcess = function (fn) {
        $.get("process", function (data) {
            me.process = data;
            var titile = $("#titile");
            if ($.isEmptyObject(data) || data.sort == 0) {
                titile.html("抽奖完成");
                showLuckyPersonList();
                me.start = function () {};
                me.ready = function () {};
            }
            else {
                titile.html(me.getCnLevel(data.level));
                if (fn) {
                    fn();
                }
            }
        });
    };

    /**
     * 开始滚动显示人员信息
     */
    function loopShow() {
        function start() {
            var num = Math.floor(Math.random() * (personNum - 1));
            var phone = arrPhone[num];
            var name = me.person[phone];
            $("#showPhone").html(phone);
            $("#showName").html(name);
        }

        looping = true;
        t = setInterval(start, 10);
    }

    /**
     * 停止滚动显示
     */
    function stopLoop() {
        looping = false;
        clearInterval(t);
        t = 0;
    }

    /**
     * 抽奖请求
     */
    function lottery(fn) {
        $.get("lottery", {}, function (data) {
            if (!$.isEmptyObject(data)) {
                if(fn){fn(data)}
                printLuckyPerson(data);
            }
        })
    }

    /**
     * 显示最终获奖人员名单
     */
    var showLuckyPersonList = function () {
        $.get("lucky", {}, function (data) {
            var box = $(".box");
            $(".jz").remove();
            $(".zjmd").remove();
            $(".footer").remove();
            var result = $('<div class="result"></div>');
            for (var i in data) {
                result.append("<h2 class='sh4'>" + me.getCnLevel(i) + "</h2>");
                var persons = data[i];
                if(Object.keys(persons).length < 10){
                    var centerSpan = $('<div class="result" style="text-align: center"></div>');
                    for (var j in persons) {
                        centerSpan.append('<span title="'+j+'" class="person masked">' + persons[j] + '</span>');
                    }
                    result.append(centerSpan);
                }
                else{
                    for (var j in persons) {
                        result.append('<span title="'+j+'" class="person masked">' + persons[j] + '</span>');
                    }
                }


            }
            box.append(result)
        })
    };

    /**
     * 显示当前级别中奖人员名单
     * 间隔延时1秒
     * @param person
     */
    function printLuckyPerson(person) {
        isReady = false;
        showing = true;
        var phone = Object.keys(person);
        var size = phone.length;
        var i = 0;
        $('#lucky').html("");
        $("#showPhone").html(phone[0]);
        $("#showName").html(person[phone[0]]);
        function addLucky() {
            var tPhone = phone[i++];
            $('#lucky').append(
                "<span class='phone masked' title='"+tPhone+"' style='margin-right: 10px;font-size: 30px;'>" + person[tPhone] + "</span>");
            $("#showPhone").html(tPhone);
            $("#showName").html(person[tPhone]);

            if (i >= size) {
                clearInterval(t);
                t = 0;
                showing = false;
            }
        }

        t = setInterval(addLucky, 1000);
    }

    /**
     * 载入所有人员列表
     */
    function loadPerson() {
        $.get("person", {}, function (data) {
            me.person = data;
            arrPhone = Object.keys(data);
            personNum = arrPhone.length;
        })
    }

    /**
     * 初始化时自动载入
     */
    loadPerson();
}