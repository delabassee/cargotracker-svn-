var xhr;

function createRequest(){
    xhr = new XMLHttpRequest();
}

function logIncident(){
    
    createRequest();
    var form = document.forms[0];
   
    //Construct the JSON data
    var data = {};
    for(var i=0,ii=form.length;i<ii;++i){
       var input = form[i];
       if(input.name){
           data[input.name] = input.value;
       }
    }
    
     xhr.open("POST", "http://localhost:8080/cargo-tracker/rest/handling/reports", true);
     xhr.setRequestHeader('Content-Type', 'application/json; charset=UTF-8');
     xhr.send(JSON.stringify(data));
     xhr.onreadystatechange = update;
}

function update(){
    if (xhr.readyState == 4) {
         result = xhr.responseText;
         if(result){
            alert(result);
         }
    }
}