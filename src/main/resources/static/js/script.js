console.log("this is my js file")

const toggleSidebar = () =>{
	
	if($(".sidebar").is(":visible")){
		 
		 //true
		 //band krna h 
		 $(".sidebar").css("display","none");
		 $(".content").css("margin-left","0%");
	}
	
	else{
		//false
		// show krna h 
		 $(".sidebar").css("display","block");
		 $(".content").css("margin-left","20%");
		
	}
	
	
};


const search = () => {
	console.log("searching");
	
	let query=$("#search-input").val();
	
	
	if(query==""){
		
			$(".search-result").hide();
	}
	else{
		console.log(query);
		
		
		let url=`https://smartcontactmanager-production-3876.up.railway.app/user/search/${query}`;
		
		fetch(url).then((response) => {
			return response.json();
		}).then((data) => {
			
			console.log(data);
			
			let text=`<div class='list-group'>`
			
			data.forEach((contact) => {
				const val = contact.cid;
				console.log(val);
				text+=`<a href='/user/${val}/contact'   class='list-group-item list-group-item-action'> ${contact.name} </a>`
			});
			
			text+=`</div>`
			
			$(".search-result").html(text);
			$(".search-result").show();
		});	
		
	}
};

const confirmDelete = () =>{
	console.log("inside confirmDelete function");
	let deleteUser=confirm("Do you really want to Delete Account? All your Contact will be Deleted ");
	if(deleteUser){
		window.location.href="https://smartcontactmanager-production-3876.up.railway.app/user/deleteuser"
			}

};