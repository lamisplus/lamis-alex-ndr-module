import React, {useState, useEffect} from 'react';
import { makeStyles } from '@material-ui/core/styles';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemSecondaryAction from '@material-ui/core/ListItemSecondaryAction';
import ListItemText from '@material-ui/core/ListItemText';
import ListItemAvatar from '@material-ui/core/ListItemAvatar';
import Checkbox from '@material-ui/core/Checkbox';
import axios from "axios";
import { BrowserRouter as Router, Route } from "react-router-dom";
import Breadcrumbs from "@material-ui/core/Breadcrumbs";
import Typography from "@material-ui/core/Typography";
import { CardBody, Card} from 'reactstrap';
import { Alert, AlertTitle } from '@material-ui/lab';
import {GiFiles} from 'react-icons/gi'; 
import Button from "@material-ui/core/Button";
import AccountBalanceIcon from '@material-ui/icons/AccountBalance';
import MaterialTable from 'material-table';
import Tooltip from '@material-ui/core/Tooltip';
import IconButton from '@material-ui/core/IconButton';
import CloudDownloadIcon from '@material-ui/icons/CloudDownload';
import { BehaviorSubject } from 'rxjs';
import { Link } from "react-router-dom";


const useStyles = makeStyles((theme) => ({
  root: {
    width: '100%',
    maxWidth: 360,
    backgroundColor: theme.palette.background.paper,
    '& > * + *': {
      marginTop: theme.spacing(2),
    },
  },
}));


export default function CheckboxListSecondary() {
  const classes = useStyles();
  const [facilities, setFacilities] = useState( [])
  const [processing, setProcessing] =  useState(0)
  const [dowloadComp, setDowloadComp] = useState(0)
  const [generateComp, setGenerateComp] = useState(0) //this is to make the generate component visible
  const [generateButton, setGenerateButton] = useState(true)
  const [generateButtonAction, setGenerateButtonAction] = useState(false)
  const [facilitiesApi, setfacilitiesApi] = useState({facilities: []})
  const [generatedNdrListed, setGeneratedNdrList] = useState( [])
  
  const [checked, setChecked] = React.useState([]);
  const [loading, setLoading] = useState('')
  
  const [user, setUser] = useState(null);

  useEffect(() => {
    fetchMe()
    generatedNdrList()
  }, []);

  let token = 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJndWVzdEBsYW1pc3BsdXMub3JnIiwiYXV0aCI6IlN1cGVyIEFkbWluIiwibmFtZSI6Ikd1ZXN0IEd1ZXN0IiwiZXhwIjoxNjMzMDczMDYyfQ.XMXtJMvmDVHBUiUr78KSvD2JIMO6pjxx3r_9USI6DP8FdUVdTbNu7eGlzgZUaqq57WfSCe-y9MkbY48uDqLwcA';
  //let token = (new URLSearchParams(window.location.search)).get("jwt")
 //const url = '/api/'
 const  url = 'http://localhost:8484/api/'
 const  url_ndr = 'http://localhost:8080/api/'
 ///GET LIST OF FACILITIES
  async function fetchMe() {

    axios
        .get(`${url_ndr}ndr_message/facilities`,
        //{ headers: {"Authorization" : `Bearer ${token}`} }
          )
        .then((response) => {
          setUser(response.data);
          console.log(response.data);
          setFacilities(response.data);
           })
        .catch((error) => {

        });
  
}

//GET LIST OF NDR GENERATED

 ///GET LIST OF FACILITIES
 async function generatedNdrList() {
  axios
      .get(`${url_ndr}ndr_message/download`)
      .then((response) => {
        setGeneratedNdrList(response.data.fileInfos);
        console.log(response.data.fileInfos);
       
         })
      .catch((error) => {
      });

}


  const handleToggle = (value) => () => {
    const currentIndex = checked.indexOf(value);
    const newChecked = [...checked];
    
    if (currentIndex === -1) {
      newChecked.push(value);
    } else {
      newChecked.splice(currentIndex, 1);
    }

    setChecked(newChecked);
  };

  const downloadComponent = (e) =>() => {
    setDowloadComp(1)
    setGenerateComp(1)
    setGenerateButton(false)
    setGenerateButtonAction(true)

  }

  const generateComponent= (e) =>() => {
    setDowloadComp(1)
    setGenerateComp(0)
    setGenerateButton(true)
    setGenerateButtonAction(false)

  }

 const  generateAction = () => {
  setProcessing(1)
  setGenerateButtonAction(true)

   let FacilityIDArray = [ {
    "id": 1432,
    "name": "Facility in Udung-Uko  Akwa Ibom"
  }];
  //LOOPING THROUGH THE FACILITIES OBJECT ARRAY TO FORM THE NEW OBJECT 
  //  checked.forEach(function(value) {
  //
  //   // delete value['id'];
  //   // delete value['applicationUserId'];
  //   // delete value['archived'];
  //   //FacilityIDArray.push(value);
  //
  //  });
   facilitiesApi['facilities'] = FacilityIDArray;
   //SENDING A POST REQUEST 
   axios.post(`${url_ndr}ndr_message/generate`, facilitiesApi)
        .then(response => {
          console.log(response.data)
          setProcessing(0)
          setDowloadComp(1)
          setGenerateComp(1)
          setGenerateButton(false)
          setGenerateButtonAction(false)
        })
        .catch(error => {
          setProcessing(0)
          setDowloadComp(1)
          setGenerateComp(0)
          setGenerateButton(true)
          setGenerateButtonAction(false)
            console.error('There was an error!', error);
        });
 }


  return (
    <div >     
      <Card>
        <CardBody>       
        <br/> 
          {processing===1 ?
            <Button
              color="primary"
              variant="contained"
              className=" float-right mr-1"
              size="large"
              
            >
              &nbsp;&nbsp;
              <span style={{textTransform: 'capitalize'}}>Generating Please Wait...  </span>
                         
            </Button>
            : 
            ""
          }       
          <Button
              color="primary"
              variant="contained"
              className=" float-right mr-1"
              size="large"
              onClick= {downloadComponent()}
              hidden={generateButtonAction}
            >
              {<GiFiles />} &nbsp;&nbsp;
              <span style={{textTransform: 'capitalize'}}>View Files  </span>
                         
            </Button>
            
        {checked.length >= 1 ? <>
            <Button
              color="primary"
              variant="contained"
              className=" float-right mr-1"
              size="large"            
              hidden={generateButtonAction}
              onClick= {() => generateAction()}
            >
              {<GiFiles />} &nbsp;&nbsp;
              <span style={{textTransform: 'capitalize'}}> Generate </span>
              
            </Button>
            </>
            :
            <>
            <Button
              color="primary"
              variant="contained"
              className=" float-right mr-1"
              size="large"
              //disabled="true"
              hidden={generateButtonAction}
              onClick= {() => generateAction()}
            >
              {<GiFiles />} &nbsp;&nbsp;
              <span style={{textTransform: 'capitalize'}}> Generate </span>
              
            </Button>
            </>
        }
          {generateComp===0 ? (
            <>
            <br/> <br/>
            <Alert severity="info">
            <AlertTitle>Info</AlertTitle>
              Please check the Facilities you want  
            </Alert>
            <br/>

          <List dense className={classes.root} >
                  
      <br/>
        {facilities.map((value) => {
          //console.log(value)
          const labelId = `checkbox-list-secondary-label-${value.id}`;
          return (
            <ListItem key={value.id} button>
              <ListItemAvatar>
                <AccountBalanceIcon />
              </ListItemAvatar>
              <ListItemText id={labelId} primary={`${value.name }`} />
              <ListItemSecondaryAction>
                <Checkbox
                  edge="end"
                  onChange={handleToggle(value)}
                  checked={checked.indexOf(value) !== -1}
                  inputProps={{ 'aria-labelledby': labelId }}
                />
              </ListItemSecondaryAction>
            </ListItem>
            
          );
          
        })}
        </List>
        </>
          ) : (
            <>
            <br/>
            <Alert severity="info">
              <AlertTitle>Info</AlertTitle>
               <strong>List of Facilities!</strong>
            </Alert>
            <br/>
            <Button
              color="primary"
              variant="contained"
              className=" float-right mr-1"
              size="large"
              hidden={generateButton}
              onClick= {generateComponent()}
            >
              {<GiFiles />} &nbsp;&nbsp;
              <span style={{textTransform: 'capitalize'}}> Back </span>
              
            </Button>
            <br/><br/><br/><br/>
            <MaterialTable
            title="List of Facilities"
            columns={[
                { title: "Facility Name", field: "name", filtering: false },
                {
                  title: "Number of Files Generated",
                  field: "files",
                  filtering: false
                },
                { title: "Date Last Generated", field: "date", type: "date" , filtering: false},          

                {
                  title: "Action",
                  field: "actions",
                  filtering: false,
                },
            ]}
            isLoading={loading}
            data={generatedNdrListed.map((row) => ({
                name: row.name,
                files:row.numberRecords,
                date: row.dateGenerated,             
                actions:  
                        <Link to={row.url+"/"+row.name} target="_blank" download>
                          <Tooltip title="Download">
                              <IconButton aria-label="Download" >
                                  <CloudDownloadIcon color="primary"/>
                              </IconButton>
                          </Tooltip>
                          </Link>

            }))}
            options={{
                
                pageSizeOptions: [5,10,50,100,150,200],
                headerStyle: {
                backgroundColor: "#9F9FA5",
                color: "#000",
                margin: "auto"
                },
                filtering: true,
                searchFieldStyle: {
                    width : '300%',
                    margingLeft: '250px',
                },
                exportButton: true,
                searchFieldAlignment: 'left',          
            }}

        />
        </>
          ) }
            
      </CardBody>
    </Card>
       
  </div>
    
  );
  
}
