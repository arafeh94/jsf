var graph = {
    _simulation: null,
    _svg: null,
    _width: null,
    _height: null,
    display: function (json) {
        d3.selectAll("svg > *").remove();
        if (this._svg == null) this._svg = d3.select("svg");
        if (this._width == null) this._width = +this._svg.attr("width");
        if (this._height == null) this._height = +this._svg.attr("height");
        if (this._simulation == null) {
            this._simulation = d3.forceSimulation()
                .force("link", d3.forceLink().id(function (d) {
                    return d.id;
                }))
                .force('charge', d3.forceManyBody()
                    .strength(-200)
                    .theta(0.8)
                    .distanceMax(150)
                )
                .force("center", d3.forceCenter(this._width / 2, this._height / 2));
        }


        var link = this._svg.append("g")
            .style("stroke", "#aaa")
            .selectAll("line")
            .data(json.links)
            .enter().append("line");

        var node = this._svg.append("g")
            .attr("class", "nodes")
            .selectAll("circle")
            .data(json.nodes)
            .enter().append("circle")
            .attr("r", "6px")
            .call(d3.drag()
                .on("start", dragStarted)
                .on("drag", dragged)
                .on("end", dragEnded));

        var label = this._svg.append("g")
            .attr("class", "labels")
            .selectAll("text")
            .data(json.nodes)
            .enter().append("text")
            .attr("class", "label")
            .text(function (d) {
                return d.id;
            });

        this._simulation
            .nodes(json.nodes)
            .on("tick", ticked);

        this._simulation.force("link")
            .links(json.links);

        function ticked() {
            link
                .attr("x1", function (d) {
                    return d.source.x;
                })
                .attr("y1", function (d) {
                    return d.source.y;
                })
                .attr("x2", function (d) {
                    return d.target.x;
                })
                .attr("y2", function (d) {
                    return d.target.y;
                });

            node
                .attr("r", 16)
                .style("fill", "#efefef")
                .style("stroke", "#424242")
                .style("stroke-width", "1px")
                .attr("cx", function (d) {
                    return d.x + 5;
                })
                .attr("cy", function (d) {
                    return d.y - 3;
                });

            label
                .attr("x", function (d) {
                    return d.x;
                })
                .attr("y", function (d) {
                    return d.y;
                })
                .style("font-size", "10px").style("fill", "#333");
        }
    }
};

function dragStarted(d) {
    if (!d3.event.active) graph._simulation.alphaTarget(0.3).restart();
    d.fx = d.x;
    d.fy = d.y;
}

function dragged(d) {
    d.fx = d3.event.x;
    d.fy = d3.event.y;
}

function dragEnded(d) {
    d.fx = d3.event.x;
    d.fy = d3.event.y;
    if (!d3.event.active) graph._simulation.alphaTarget(0);
}
