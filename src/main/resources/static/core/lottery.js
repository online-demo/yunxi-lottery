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
                return level;
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
     * 特殊的抽奖步骤
     * @param type = 1 时为替换已经中奖人员
     *        type = 2 时为额外的抽奖
     * @param award 特殊奖项名称
     * @param num 抽奖个数
     */
    this.specialLottery = function (award, num) {
        if (isReady) {
            if (!looping) {
                loopShow();
            }
            else {
                $.get("specialLottery", {
                    award: award,
                    num: num
                }, function (data) {
                    stopLoop();
                    if (!$.isEmptyObject(data)) {
                        printLuckyPerson(data);
                    }
                })
            }
        }
    };

    this.replaced = function (award, phone) {
        if (isReady) {
            if (!looping) {
                loopShow();
            }
            else {
                $.get("replaced", {
                    award: award,
                    phone: phone
                }, function (data) {
                    stopLoop();
                    if (!$.isEmptyObject(data)) {
                        printLuckyPerson(data);
                    }
                })
            }
        }
    };

    /**
     * 初始化信息,准备抽奖
     * 防止多次触发抽奖操作
     */
    me.ready = function (info) {
        if (!showing && !isReady) {
            isReady = true;
            $("#info").html(info);
            $('#lucky').html("");
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
                me.start = function () {
                };
                me.ready = function () {
                };
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
        $('#lucky').html("");
        function start() {
            var num = Math.floor(Math.random() * (personNum - 1));
            var phone = arrPhone[num];
            var name = me.person[phone];
            $("#info").html(phone+" "+name);
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
                if (fn) {
                    fn(data)
                }
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
                if (Object.keys(persons).length < 10) {
                    var centerSpan = $('<div class="result" style="text-align: center"></div>');
                    for (var j in persons) {
                        centerSpan.append('<span title="' + j + '" class="person">' + persons[j] + '</span>');
                    }
                    result.append(centerSpan);
                }
                else {
                    for (var j in persons) {
                        result.append('<span title="' + j + '" class="person">' + persons[j] + '</span>');
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
        $("#info").html(phone[0]+" "+person[phone[0]]);
        function addLucky() {
            var tPhone = phone[i++];
            $('#lucky').append(
                "<span class='phone masked LuckyPerson' title='" + tPhone + "' style='margin-right: 10px;font-size: 30px;'>" + person[tPhone] + "</span>");
            $("#info").html(tPhone+" "+person[tPhone]);

            if (i >= size) {
                clearInterval(t);
                t = 0;
                showing = false;
            }
        }

        t = setInterval(addLucky, 1000);
    }

    $("#lucky").on('dblclick','.LuckyPerson',function () {
        var btn = $(this);
        var phone = btn.attr('title');
        /*<![CDATA[*/
        window.open("award?type=2&award="+me.process.level+"&phone="+phone+"&name="+btn.html());
        /*]]>*/
    });

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