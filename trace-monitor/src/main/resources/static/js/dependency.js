const dagre = window.dagreD3;

$(function () {
    const loadingHtml =
        "<div class='col-xs-12 text-center'>"
        + "<img src='/static/images/loading.gif'/> 数据加载中..."
        + "</div>";

    function searchDateType(dateType) {
        $("#loadingContainer").html(loadingHtml);
        setTimeout(function () {
            $.ajax(
                "/dependencies.html",
                {
                    type: 'GET',
                    async: false,
                    data: {"dateType": dateType},
                    success: data => {
                        const links = data.data;
                        buildServiceData(links);
                        dependencyDataReceived(links);
                        $("#loadingContainer").html("");
                    }
                }
            );
        }, 1000);
    }

    /**
     * 初始化时间类型搜索
     */
    $(".js-dependency-group").on("click", function (e) {

        $(".js-dependency-self").removeClass("active");
        if ($("#dependencyQueryForm").is(":visible")) {
            $("#dependencyQueryForm").hide();
        }

        e.preventDefault();
        $(".js-dependency-group").removeClass("active");
        $(e.target).addClass("active");
        const dateType = e.target.getAttribute("data-type");
        searchDateType(dateType);
    });

    $(".js-dependency-self").on("click", function (e) {
        e.preventDefault();
        $("#dependencyQueryForm").show();

        $(".js-dependency-group").removeClass("active");
        $(e.target).addClass("active");
    });

    /**
     * 初始化时间类型搜索
     */
    $('#firstDateType').click();

    /**
     * 组件时间初始化
     */
    function initDateComponent() {

        // 初始化日期控件
        $("#startTime").datetimepicker({
            language: "zh-CN",
            autoclose: true,
            todayBtn: true,
            format: "yyyy-mm-dd hh:ii:ss",
            startView: 2,
            minView: 0
        }).on('changeDate', function (selected) {
            const minDate = new Date(selected.date.valueOf());
            $('#endTime').datetimepicker('setStartDate', minDate);
        });

        $("#endTime").datetimepicker({
            language: "zh-CN",
            autoclose: true,
            todayBtn: true,
            format: "yyyy-mm-dd hh:ii:ss",
            startView: 2,
            minView: 0,
            initialDate: new Date()
        }).on('changeDate', function (selected) {
            const maxDate = new Date(selected.date.valueOf());
            $('#startTime').datetimepicker('setEndDate', maxDate);
        });
    }

    // 时间控件
    initDateComponent();

    let services = {};
    let dependencies = {};

    function getDependency(inputData) {
        $("#loadingContainer").html(loadingHtml);
        setTimeout(function () {
            $.ajax(
                "/dependencies.html",
                {
                    type: 'GET',
                    async: false,
                    data: inputData,
                    success: data => {
                        const links = data.data;
                        buildServiceData(links);
                        dependencyDataReceived(links);
                        $("#loadingContainer").html("");
                    }
                }
            );
        }, 1000);
    }

    // 依赖分析
    $(".js-dependency-analyze").on("click", function () {
        $("#dependency-search-alert").hide();

        const startTime = $("#startTime").val();
        const endTime = $("#endTime").val();

        if (startTime === '' || endTime === '') {
            $("#dependency-error-container").text("开始时间和结束时间需都填");
            $("#dependency-search-alert").show();
            return false;
        }

        const now = new Date().getTime();
        const start = Date.parse(startTime);
        if(now - start > 30 * 24 * 3600 * 1000){
            $("#dependency-error-container").text("开始时间只能在1个月之内");
            $("#dependency-search-alert").show();
            return false;
        }

        getDependency($("#dependencyQueryForm").serialize());
    });

    // 查询调用链错误信息隐藏
    $(".js-close-dependency-alert").on("click", function () {
        $("#dependency-search-alert").hide();
    });

    function buildServiceData(links) {
        services = {};
        dependencies = {};

        links.forEach(link => {
            const {parent, child} = link;

            dependencies[parent] = dependencies[parent] || {};
            dependencies[parent][child] = link;

            services[parent] = services[parent] || {serviceName: parent, uses: [], usedBy: []};
            services[child] = services[child] || {serviceName: child, uses: [], usedBy: []};

            services[parent].uses.push(child);
            services[child].usedBy.push(parent);
        });
    }

    function dependencyDataReceived(links) {
        const lowErrorRate = 0.01;
        const highErrorRate = 0.1;

        const container = $("#dependency-container")[0];
        const rootSvg = container.querySelector('svg');
        rootSvg.textContent = null;

        const svg = d3.select('svg');
        const svgGroup = svg.append('g');

        const g = new dagre.Digraph();
        const renderer = new dagre.Renderer();

        function arrayUnique(array) {
            return array.filter((val, i, arr) => i <= arr.indexOf(val));
        }

        function flatten(arrayOfArrays) {
            if (arrayOfArrays.length === 0) {
                return [];
            } else {
                return arrayOfArrays.reduce((a, b) => a.concat(b));
            }
        }

        function getIncidentEdgeElements(nodeName) {
            const selectedElements = rootSvg
                .querySelectorAll(`[data-from='${nodeName}'],[data-to='${nodeName}']`);
            return [...selectedElements];
        }

        function getIncidentNodeElements(from, to) {
            return [
                rootSvg.querySelector(`[data-node='${from}']`),
                rootSvg.querySelector(`[data-node='${to}']`)
            ];
        }

        function getAdjacentNodeElements(centerNode) {
            const edges = g.incidentEdges(centerNode);
            const nodes = flatten(edges.map(edge => g.incidentNodes(edge)));
            const otherNodes = arrayUnique(nodes.filter(node => node !== centerNode));
            const elements = otherNodes.map(name => rootSvg.querySelector(`[data-node='${name}']`));
            return elements;
        }

        function scale(i, startRange, endRange, minResult, maxResult) {
            return minResult + (i - startRange) * (maxResult - minResult) / (endRange - startRange);
        }

        // Find min/max number of calls for all dependency links
        // to render different arrow widths depending on number of calls
        let minCallCount = 0;
        let maxCallCount = 0;
        links.filter(link => link.parent !== link.child).forEach(link => {
            const numCalls = link.callCount;
            if (minCallCount === 0 || numCalls < minCallCount) {
                minCallCount = numCalls;
            }
            if (numCalls > maxCallCount) {
                maxCallCount = numCalls;
            }
        });
        const minLg = Math.log(minCallCount);
        const maxLg = Math.log(maxCallCount);

        function arrowWidth(callCount) {
            const lg = Math.log(callCount);
            return scale(lg, minLg, maxLg, 0.3, 3);
        }

        // Get the names of all nodes in the graph
        const parentNames = links.map(link => link.parent);
        const childNames = links.map(link => link.child);
        const allNames = arrayUnique(parentNames.concat(childNames));

        // Add nodes/service names to the graph
        allNames.forEach(name => {
            g.addNode(name, {label: name});
        });

        // Add edges/dependency links to the graph
        links.filter(link => link.parent !== link.child)
            .forEach(({parent, child, callCount, errorCount}) => {
                g.addEdge(`${parent}->${child}`, parent, child, {
                    from: parent,
                    to: child,
                    callCount,
                    errorCount
                });
            });

        const layout = dagre.layout()
            .nodeSep(30)
            .rankSep(200)
            .rankDir('LR'); // LR = left-to-right, TB = top-to-bottom.

        // Override drawNodes and drawEdgePaths, so we can add
        // hover functionality on top of Dagre.
        const innerDrawNodes = renderer.drawNodes();
        const innerDrawEdgePaths = renderer.drawEdgePaths();

        renderer.drawNodes((gInner, svgInner) => {
            const svgNodes = innerDrawNodes(gInner, svgInner);
            // Add mouse hover/click handlers
            svgNodes.attr('data-node', d => d)
                .each(function (d) {
                    const $this = $(this);
                    const nodeEl = $this[0];

                    $this.click(() => {
                        renderServiceDataModal(d);
                    });

                    $this.hover(() => {
                        nodeEl.classList.add('hover');
                        rootSvg.classList.add('dark');
                        getIncidentEdgeElements(d).forEach(el => {
                            el.classList.add('hover-edge');
                        });
                        getAdjacentNodeElements(d).forEach(el => {
                            el.classList.add('hover-light');
                        });
                    }, () => {
                        nodeEl.classList.remove('hover');
                        rootSvg.classList.remove('dark');
                        getIncidentEdgeElements(d).forEach(e => {
                            e.classList.remove('hover-edge');
                        });
                        getAdjacentNodeElements(d).forEach(e => {
                            e.classList.remove('hover-light');
                        });
                    });
                });
            return svgNodes;
        });

        renderer.drawEdgePaths((gInner, svgInner) => {
            const svgNodes = innerDrawEdgePaths(gInner, svgInner);
            svgNodes.each(function (edge) {
                // Add mouse hover handlers
                const edgeEl = this;
                const $el = $(edgeEl);

                const callCount = gInner.edge(edge).callCount;
                const arrowWidthPx = `${arrowWidth(callCount)}px`;
                $el.css('stroke-width', arrowWidthPx);

                const errorCount = gInner.edge(edge).errorCount || 0;
                const errorRate = errorCount / callCount;
                if (errorRate >= highErrorRate) {
                    $el.css('stroke', 'rgb(230, 162, 161)');
                } else if (errorRate >= lowErrorRate) {
                    $el.css('stroke', 'rgb(230, 215, 140)');
                }

                $el.hover(() => {
                    rootSvg.classList.add('dark');
                    const nodes = getIncidentNodeElements(
                        edgeEl.getAttribute('data-from'),
                        edgeEl.getAttribute('data-to'));
                    nodes.forEach(el => {
                        el.classList.add('hover');
                    });
                    edgeEl.classList.add('hover-edge');
                }, () => {
                    rootSvg.classList.remove('dark');
                    const nodes = getIncidentNodeElements(
                        edgeEl.getAttribute('data-from'),
                        edgeEl.getAttribute('data-to'));
                    nodes.forEach(el => {
                        el.classList.remove('hover');
                    });
                    edgeEl.classList.remove('hover-edge');
                });
            });

            svgNodes.attr('data-from', d => gInner.edge(d).from);
            svgNodes.attr('data-to', d => gInner.edge(d).to);
            return svgNodes;
        });

        renderer
            .layout(layout)
            .run(g, svgGroup);
    }

    function renderServiceDataModal(d) {
        const data = services[d];
        const $modal = $('#serviceModal');
        $modal.find('#serviceUsedByList').html('');
        data.usedBy.sort((a, b) =>
            a.toLowerCase().localeCompare(b.toLowerCase())
        );
        data.usedBy.forEach(usedBy => {
            const $name = $(`<li><a href="">${usedBy}</a></li>`);
            $name.find('a').click(ev => {
                ev.preventDefault();
                renderDependencyModal({
                    parent: usedBy,
                    child: data.serviceName
                });
            });
            $modal.find('#serviceUsedByList').append($name);
        });

        $modal.find('#serviceUsesList').html('');
        data.uses.sort((a, b) =>
            a.toLowerCase().localeCompare(b.toLowerCase())
        );

        data.uses.forEach(uses => {
            const $name = $(`<li><a href="">${uses}</a></li>`);
            $name.find('a').click(ev => {
                ev.preventDefault();
                renderDependencyModal({
                    parent: data.serviceName,
                    child: uses
                });
            });
            $modal.find('#serviceUsesList').append($name);
        });

        $modal.find('#serviceModalTitle').text(data.serviceName);

        $modal.modal('show');
        $('#dependencyModal').modal('hide');
    }

    function renderDependencyModal(data) {
        const $modal = $('#dependencyModal');
        const $parentElement = $(`<a href="">${data.parent}</a>`);
        $parentElement.click(ev => {
            ev.preventDefault();
            renderServiceDataModal(data.parent);
        });

        const $childElement = $(`<a href="">${data.child}</a>`);
        $childElement.click(ev => {
            ev.preventDefault();
            renderServiceDataModal(data.child);
        });

        $modal.find('#dependencyModalParent').html($parentElement);
        $modal.find('#dependencyModalChild').html($childElement);

        const link = dependencies[data.parent][data.child]

        $modal.find('#dependencyCallCount').text(link.callCount);
        $modal.find('#dependencyErrorCount').text(link.errorCount || 0);
        $modal.find('#dependencyErrorRate').text((link.errorRate || '0') + "%");
        $modal.find('#dependencyTp90').text((link.tp90 || '0') + 'ms');
        $modal.find('#dependencyTp99').text((link.tp99 || '0') + 'ms');
        $modal.find('#dependencyTp999').text((link.tp999 || '0') + 'ms');
        $modal.find('#dependencyTp9999').text((link.tp9999 || '0') + 'ms');

        $('#serviceModal').modal('hide');
        $modal.modal('show');
    }
});
