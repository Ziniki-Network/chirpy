require(['zinc'], function(zinc) {
  var promise = zinc.newRequestor("http://localhost:8380/chirpy");
  console.log();
  promise.then(function(me) {
    try {
      var currentRequest = null;
      var currentTopic = null;
      function addTopic(topic) {
        console.log(topic);
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
      
      $('#sendMessage').click(function(ev) {
        if (!currentTopic) {
          alert('You must select a topic before posting');
          return;
        }
        var userName = $('#user').val();
        var req = me.create("topic/" + currentTopic);
        req.setPayload({"message":{"text": userName + ": " + $('#entry').val()}});
        req.send();
      });
    } catch (e) { console.log(e); }
  });
});