//atepicker
$(function(){
  $('.datepicker').datepicker({
    "format": 'mm-dd-yyyy',
    "autoclose": true
  });
});

//Import Data
d3.json("json/recipient.json", function(error, result) {
    renderRecipList(result);
})

d3.json("json/sample.json", function(error, result) {
    renderWeekList(result);
    renderChart1(result);
    renderChart2(result);
})

d3.json("json/keyword.json", function(error, result) {
    renderChart3(result);
})

// Render functions  
function renderChart1(data) {
    //Left side: Week List
    var chartWidth = 700;
    var chartHeight = 350;
    var charMargin = {top: 40, left: 40, right: 40, bottom: 40};
    var chartInnerWidth = chartWidth - charMargin.left - charMargin.right;
    var chartInnerHeight = chartHeight - charMargin.top - charMargin.bottom;
    var chart1 = d3.select("#chart1");

    chart1.attr("width", chartWidth)
         .attr("height", chartHeight)
         .attr("transform", "translate(" + charMargin.left + "," + charMargin.top + ")");
    
    var xScale = d3.scale.ordinal()
        .rangeBands([0, chartInnerWidth])
        //.range([0, 1, 2,3,4,5,6,7,8,9,10,11,12])
        .domain(["0:00", "2:00", "4:00", "6:00", "8:00", "10:00", "12:00", "14:00", "16:00", "18:00", "20:00", "22:00", "24:00"]);
        

    var yScale = d3.scale.ordinal()
        .rangeBands([chartInnerHeight, 0])
        .domain(data.map(function(d) { return d.day; }));

    var cScale = d3.scale.linear()
        .range([yScale.rangeBand(), 0])
        .domain([5, 0]);

    var xAxis = d3.svg.axis()
        .scale(xScale)
        .orient("bottom");

    var yAxis = d3.svg.axis()
        .scale(yScale)
        .orient("left");

    chart1.append("g")
        .attr("class", "axis")
        .attr("transform", "translate(" + charMargin.left + ", " + (chartInnerHeight + charMargin.top) + ")")
        .call(xAxis);

    chart1.append("g")
        .attr("class", "axis")
        .attr("transform", "translate(" + charMargin.left + ", " + charMargin.top + ")")
        .call(yAxis); 
}

function renderChart2(data) {
    var chartWidth = 350;
    var chartHeight = 200;
    var charMargin = {top: 0, left: 40, right: 40, bottom: 20};
    var chartInnerWidth = chartWidth - charMargin.left - charMargin.right;
    var chartInnerHeight = chartHeight - charMargin.top - charMargin.bottom;
    var chart2 = d3.select("#chart2");

    chart2.attr("width", chartWidth)
         .attr("height", chartHeight)
         .attr("transform", "translate(" + charMargin.left + "," + charMargin.top + ")");;

    var xScale = d3.scale.linear()
        .range([0, chartInnerWidth])
        .domain([0, d3.max(data, function(d) { return d.recipient; })]);

    var yScale = d3.scale.ordinal()
        .rangeRoundBands([chartInnerHeight, 0], .2)
        .domain(data.map(function(d) { return d.day; }));

    var xAxis = d3.svg.axis()
        .scale(xScale)
        .orient("bottom");

    var yAxis = d3.svg.axis()
        .scale(yScale)
        .orient("left");

    chart2.append("g")
        .attr("class", "axis")
        .attr("transform", "translate(" + charMargin.left + ", " + (chartInnerHeight + charMargin.top) + ")")
        .call(xAxis);

    chart2.append("g")
        .attr("class", "axis")
        .attr("transform", "translate(" + charMargin.left + ", " + charMargin.top + ")")
        .call(yAxis);

    chart2.append("text")
        .text("Recipient")
        .style({"font-size": "15px", fill: "#ccc"})
        .attr("dx", 250)
        .attr("dy", 150);

    chart2.selectAll(".bar")
      .data(data)
      .enter()
      .append("rect")
      .attr("class", "bar")
      //.attr("x", charMargin.left)
      .attr("y", function(d) { return yScale(d.day); })
      .attr("width", function(d) { return xScale(d.recipient); })
      .attr("height", yScale.rangeBand())
      .attr("transform", "translate(" + charMargin.left + ", " + charMargin.top + ")")
      .attr("fill", "#5bc0de");   
}

function renderChart3(data) {
    var chartWidth = 350;
    var chartHeight = 200;
    var charMargin = {top: 0, left: 60, right: 40, bottom: 20};
    var chartInnerWidth = chartWidth - charMargin.left - charMargin.right;
    var chartInnerHeight = chartHeight - charMargin.top - charMargin.bottom;
    var chart3 = d3.select("#chart3");

    chart3.attr("width", chartWidth)
         .attr("height", chartHeight)
         .attr("transform", "translate(" + charMargin.left + "," + charMargin.top + ")");;

    var xScale = d3.scale.linear()
        .range([0, chartInnerWidth])
        .domain([0, d3.max(data, function(d) { return d.frequency; })]);

    var yScale = d3.scale.ordinal()
        .rangeRoundBands([chartInnerHeight, 0], .2)
        .domain(data.map(function(d) { return d.keyword; }));

    var xAxis = d3.svg.axis()
        .scale(xScale)
        .orient("bottom");

    var yAxis = d3.svg.axis()
        .scale(yScale)
        .orient("left");

    chart3.append("g")
        .attr("class", "axis")
        .attr("transform", "translate(" + charMargin.left + ", " + (chartInnerHeight + charMargin.top) + ")")
        .call(xAxis);

    chart3.append("g")
        .attr("class", "axis")
        .attr("transform", "translate(" + charMargin.left + ", " + charMargin.top + ")")
        .call(yAxis);

    chart3.append("text")
        .text("Keyword")
        .style({"font-size": "15px", fill: "#ccc"})
        .attr("dx", 250)
        .attr("dy", 150);

    chart3.selectAll(".bar")
      .data(data)
      .enter()
      .append("rect")
      .attr("class", "bar")
      //.attr("x", charMargin.left)
      .attr("y", function(d) { return yScale(d.keyword); })
      .attr("width", function(d) { return xScale(d.frequency); })
      .attr("height", yScale.rangeBand())
      .attr("transform", "translate(" + charMargin.left + ", " + charMargin.top + ")")
      .attr("fill", "#5cb85c");  
}

function renderWeekList(data) {
    //Left side: Week List
    var chartWidth = 260;
    var chartHeight = 140;
    var charMargin = {top: 10, left: 20, right: 80, bottom: 20};
    var chartInnerWidth = chartWidth - charMargin.left - charMargin.right;
    var chartInnerHeight = chartHeight - charMargin.top - charMargin.bottom;
    var week1 = d3.selectAll(".weeklist");

    week1.attr("width", chartWidth)
         .attr("height", chartHeight)
         .attr("transform", "translate(" + charMargin.left + "," + charMargin.top + ")");;
    
    var xScale = d3.scale.ordinal()
        .rangeRoundBands([0, chartInnerWidth], .1)
        .domain(data.map(function(d) { return d.day; }));

    var yScale = d3.scale.linear()
        .range([chartInnerHeight, 0])
        .domain([0, d3.max(data, function(d) { return d.total; })]);

    var xAxis = d3.svg.axis()
        .scale(xScale)
        .orient("bottom");

    var yAxis = d3.svg.axis()
        .scale(yScale)
        .orient("left");

    week1.append("g")
        .attr("class", "axis")
        .attr("transform", "translate(" + charMargin.left + ", " + (chartInnerHeight + charMargin.top) + ")")
        .call(xAxis);

    week1.append("g")
        .attr("class", "axis")
        .attr("transform", "translate(" + charMargin.left + ", " + charMargin.top + ")")
        .call(yAxis);

    week1.append("text")
        .text("Week 1")
        .style({"font-size": "15px", fill: "#ccc"})
        .attr("dx", 200)
        .attr("dy", 70);

    week1.selectAll(".bar")
      .data(data)
      .enter()
      .append("rect")
      .attr("class", "bar")
      .attr("x", function(d) { return xScale(d.day); })
      .attr("y", function(d) { return yScale(d.total); })
      .attr("width", xScale.rangeBand())
      .attr("height", function(d) { return chartInnerHeight - yScale(d.total); })
      .attr("transform", "translate(" + charMargin.left + ", " + charMargin.top + ")")
      .attr("fill", "#337ab7");  
}

function renderRecipList(data) {
    //Right side: Recipient list
    d3.select("#recipList").selectAll('tr')
        .data(data)
        .enter()
        .append('tr')
        .attr("class", "text-primary")
        .text(function(d) {return d.name});
}








