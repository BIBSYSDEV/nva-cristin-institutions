# nva-cristin-institutions

Lambda for fetching institution and unit data from the [Cristin API](https://api.cristin.no/v2/doc/index.html)


### GET cristin-institutions?{parameters}

| Query parameter | Description |
| ------ | ------ |
| name | Name, part of name or acronym. Accepts letters, digits, dash and whitespace. (Mandatory) |
| language | Preferred language for names. Accepts 'nb' or 'en'. (Optional) |


#### Response

Returns a JSON array of up to 5 institutions, or an empty JSON array if no institutions are found.

Example response body:

```json
[
  {
    "cristinInstitutionId": "194",
    "institutionNames": [
      {
        "name": "Norges teknisk-naturvitenskapelige universitet",
        "language": "nb"
      }
    ],
    "acronym": "NTNU",
    "country": "NO",
    "cristinUnitId": "194.0.0.0"
  },
  {
    "cristinInstitutionId": "43200063",
    "institutionNames": [
      {
        "name": "National Taiwan Normal University",
        "language": "nb"
      }
    ],
    "acronym": "NTNU",
    "country": "TW",
    "cristinUnitId": "43200063.0.0.0"
  }
]
```


#### HTTP Status Codes

* 200 - Ok, returns an array of 0-5 institutions.
* 400 - Bad request, returned if the parameters are invalid.
* 500 - Internal server error, returned if a problem is encountered retrieving institution data


### GET cristin-institutions/unit/{id}?

| Path parameter | Description |
| ------ | ------ |
| id | A cristinUnitId. (Mandatory) |

| Query parameter | Description |
| ------ | ------ |
| language | Preferred language for names. Accepts 'nb' or 'en'. (Optional) |


#### Response

Returns a JSON array of all subunits for the provided unit (cristinUnitId), or an empty JSON array if the provided unit does not have any subunits.

Example response body:

```json
[
  {
    "cristinUnitId": "194.63.10.0",
    "unitNames": [
      {
        "name": "Institutt for datateknologi og informatikk",
        "language": "nb"
      }
    ]
  },
  {
    "cristinUnitId": "194.63.20.0",
    "unitNames": [
      {
        "name": "Institutt for elkraftteknikk",
        "language": "nb"
      }
    ]
  },
  {
    "cristinUnitId": "194.63.25.0",
    "unitNames": [
      {
        "name": "Institutt for teknisk kybernetikk",
        "language": "nb"
      }
    ]
  },
  {
    "cristinUnitId": "194.63.30.0",
    "unitNames": [
      {
        "name": "Institutt for informasjonssikkerhet og kommunikasjonsteknologi",
        "language": "nb"
      }
    ]
  }
]
```


#### HTTP Status Codes

* 200 - Ok, returns an array of 0-n subunits.
* 400 - Bad request, returned if the parameters are invalid.
* 500 - Internal server error, returned if a problem is encountered retrieving unit data