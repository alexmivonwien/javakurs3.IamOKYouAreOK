
/**

function showFilename() {
	
	var progress = document.querySelector('.percent');
	// Reset progress indicator on new file selection.
    progress.style.width = '0%';
    progress.textContent = '0%';
	
	if (window.File) {
		var selectedFile = document.getElementById('videoUploadForm:fileInput').files[0];
		document.getElementById('videoUploadForm:fileName').value = selectedFile.name;
	}
}


function SSEvents(){
	if(typeof(EventSource) !== "undefined") {
	    var source = new EventSource("/videostore_jsf2spring/GetUsersCounter");
	    source.onmessage = function(event) {
	        document.getElementById("usersOnline").innerHTML = event.data + " users online";
	    };
	} else {
	    document.getElementById("usersOnline").innerHTML = "Sorry, your browser does not support server-sent events...";
	}
}



function handleFileSelect() {
	
	var selectedFile = document.getElementById('videoUploadForm:fileInput').files[0];
	
	var progress = document.querySelector('.percent');
    // Reset progress indicator on new file selection.
    progress.style.width = '0%';
    progress.textContent = '0%';

    reader = new FileReader();
    reader.onerror = errorHandler;
    reader.onprogress = updateProgress;
    
    reader.onabort = function(e) {
      alert('File read cancelled');
    };
    
    reader.onloadstart = function(e) {
      document.getElementById('progress_bar').className = 'loading';
    };
    
    reader.onload = function(e) {
      // Ensure that the progress bar displays 100% at the end.
      progress.style.width = '100%';
      progress.textContent = '100%';
      setTimeout("document.getElementById('progress_bar').className='';", 2000);
    }
    
    // Read in the image file as a binary string.
    reader.readAsBinaryString(selectedFile);
  }

function updateProgress(evt) {
    // evt is an ProgressEvent.
    if (evt.lengthComputable) {
      var percentLoaded = Math.round((evt.loaded / evt.total) * 100);
      // Increase the progress bar length.
      if (percentLoaded < 100) {
        progress.style.width = percentLoaded + '%';
        progress.textContent = percentLoaded + '%';
      }
    }
  }

function errorHandler(evt) {
    switch(evt.target.error.code) {
      case evt.target.error.NOT_FOUND_ERR:
        alert('File Not Found!');
        break;
      case evt.target.error.NOT_READABLE_ERR:
        alert('File is not readable');
        break;
      case evt.target.error.ABORT_ERR:
        break; // noop
      default:
        alert('An error occurred reading this file.');
    };
  }


function fileUpload() {
	if (window.File) {
		var selectedFile = document.getElementById('videoUploadForm:fileInput').files[0];

		var xhr = new XMLHttpRequest();
		this.xhr = xhr;
		var reader = new FileReader(); 
		var self = this;
		var progress = document.querySelector('.percent');
		
	    this.xhr.upload.addEventListener("progress", function(e) {
	        if (e.lengthComputable) {
	          var percentLoaded = Math.round((e.loaded * 100) / e.total);
	          if (percentLoaded < 100) {
	            // Increase the progress bar length.
	            progress.style.width = percentLoaded + '%';
	            progress.textContent = percentLoaded + '%';
	          }
	        }
	      }, false);
	    
	    xhr.upload.addEventListener("load", function(e){
	    	alert ("loadEvent");
	      }, false);
	    
	    
	    xhr.open("POST", "http://localhost:8080/videostore_jsf2spring/UploadServlet");
	    xhr.overrideMimeType('text/plain; charset=x-user-defined-binary');
	    reader.onload = function(evt) {
	    	xhr.sendAsBinary(evt.target.result);
	    };
	    reader.onerror = errorHandler;
	    reader.readAsBinaryString(selectedFile);
	}    	
}
**/