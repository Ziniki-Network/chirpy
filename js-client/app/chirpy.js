require(['zinc'], function(zinc) {
  var promise = zinc.newRequestor("/chirpy");
  console.log();
  promise.then(function(me) {
    try {
      var currentRequest = null;
      var currentTopic = null;
      function addTopic(topic) {
        var item = $("<li class='topic'>"+topic+"</li>");
        item.click(function(e) { subscribeTo(topic); });
        $("#topics").append(item);
      }
      function subscribeTo(topic) {
        if (currentRequest != null) {
          currentRequest.unsubscribe();
        }
        $("li.message").remove();
        currentTopic = topic;
        currentRequest = me.subscribe("topic/" + topic, function(msg) {
          $("#messages").append("<li class='message'>"+msg.message.text+"</li>");
        });
        currentRequest.send();
      }
      
      var topics = me.subscribe("topics", function(msg) {
        addTopic(msg.topic.name);
        var topic = msg.topic.name;
      });
      topics.send();

      $('#createtopic').click(function(ev) {
        var req = me.create("topics");
        req.setPayload({"topic":$('#newtopic').val()});
        req.send();
      });      
      $('#sendMessage').click(function(ev) {
        if (!currentTopic) {
          alert('You must select a topic before posting');
          return;
        }
        var userName = $('#user').val();
        var req = me.create("topic/" + currentTopic);
        var dt = new Date();
        var m = dt.getMonth()+1;
        var d = dt.getDate();
        var h = dt.getHours();
        var ms = dt.getMinutes();
        var td = (m<10?"0":"")+m+"/"+(d<10?"0":"")+d+":"+(h<10?"0":"")+h+(ms<10?"0":"") +ms;
        req.setPayload({"message":{"text": "["+td+"] "+userName + ": " + $('#entry').val()}});
        req.send();
      });
    } catch (e) { console.log(e); }
  });
});