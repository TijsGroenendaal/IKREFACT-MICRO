# Project Bouncer

# Architecture

This repository is fork of a private repository that contains the original source code for Project Bouncer. 
Project Bouncer was an group assignment by the University of Applied Sciences Leiden to create a system to verify sex adverts on existing online website's.

The old code was implemented as a Monolith where all features where contained in a single programme.
For an elective course I was tasked with implementing the Microservice Architecture. See the following image.

![New Architecture](https://i.imgur.com/izZFlGL.png)

The project source is divided into several modules.

| Name           | Description                                                                             |
|:---------------|:----------------------------------------------------------------------------------------|
| Base           | Contains Entity and utility classes that can be used in other Modules                   |
| Bouncer        | The monolith where some features have been pulled out and places into new microservice. |
| Gateway        | The reversed proxy used by the Front-end to easily communicate with the microservices   |
| PresetService  | The Service that contains all features of the Preset domain                             |
| Security       | Contains the implementation for handling JWT security                                   |
| WebhookService | The Service that contains all features of the Webhook domain                            |

## Gateway
To configure the routing to downstream services open [application.properties](https://github.com/TijsGroenendaal/IKREFACT-MICRO/blob/7c3ebeddfd5c4012972a83af9903268ba13ba249/gateway/src/main/resources/application.properties)

Because the stripped monolith Bouncer contains multiple domains it is important that the routing configurations has the highest index. That way it will not be overwritten.

#General Usage

## Verifying verifications using challenges
A moderator can view verifications, their challenges and all data that comes with a challenge, such as images.
When someone needs to be verified a verification should be opened. Then using this verification, challenges can be opened.
Challenges must be created using presets, a preset is a predefined question/task with certain automatic validation features enabled.

Once a challenge is created, an image can be uploaded to it. When this happens, the automatic tests will run and extract additional information from the image. Now a moderator can choose to accept or reject a challenge.

When a challenge is rejected a new one can be created using a preset. Or the moderator can close the entire verification if the moderator thinks the verification is going nowhere. A verification will automatically be closed once the maximum number of challenges or days has been reached. These settings can be changed per platform by the admin.

If a challenge were to be accepted, the entire verification will be marked as accepted.

All of the above events can be captured by webhooks and forwarded to the user, using your own back-end.

### Flowchart of verification process
<a href="https://i.imgur.com/sh0Tv3N.png" target="_blank">Open image in full</a>
![Flowchart of verification process](https://i.imgur.com/sh0Tv3N.png)


## Managing predefs & presets
### Presets
Presets are used to create new challenges. Presets contain the challenge text, which usually contains the question or task that must be completed.
They also contain settings for automatic checks, more info about these can be found at the [image data processing](https://github.com/jely2002/IPSEN3-CKM-FRONT/wiki/Image-data-processing) page.
Only admins can manage presets, moderators can only view a list of their names to create a new challenge with.

### Predefs
Predefs is short for predefined's. This are predefined reject or accept reasons that can be used by moderators, to quickly provide a reason to a verdict. Only admins can manage predefs, while moderators can list them and use them in their verdicts.

# Image data processing

## Security
Images are stored either on S3 storage or locally on the server. When stored, the images will not have an extension (.jpg) and are named with a UUID that corresponds to the media ID in the database. Before an image is saved, it is encrypted with a unique key per platform, if this key is changed it will render all saved images useless. Images are encrypted so even when S3 or the server is breached, without the database nothing can be decrypted.

## EXIF
The media package contains an EXIF extractor. This is an API that assists in the reviewing of images so that a user can be verified. Files contain “meta data”, data which describes data. In the case of images this can contain compression data, lens data and much more. Whenever an image gets uploaded to our API, the EXIF data will be read and recorded automatically.

**Date/Time Original:** This is the moment the shutter closes, and the image is digitized.

**Flash:** Allows us to see if flash was used when the photo was taken.

**File Modified Date:** This records the date the image was last changed.

**GPS Data:** This contains latitude, longitude, GPS references and the GPS time stamp, this can be used to verify whether a picture was taken at a specific place. The GPS time stamp is a date accurate to the day.

**Model:** This refers to the device the picture was taken on.

If need be, this can be changed to also record extra or other data. When an image gets deleted, the EXIF data will be deleted as well to comply with GDPR. The GDPR has been kept in mind with all the data stored and can be retrieved and deleted along with all the connections if the need arises.

## Google Cloud Vision AI
[GCP Vision AI](https://cloud.google.com/vision) in short, can be used to derive insights from images using machine learning. It can extract lots of additional data from an image. Since Google has had a huge dataset to train their models on.

Google stores a submitted image so it can analyse it, after having returned the results, the image is deleted from their servers. If this fails in any way, all images are forcibly removed after a couple of hours. Google also logs some metadata, such as the file size and the time of the request.
For more info see the [Vision AI data usage policy](https://cloud.google.com/vision/docs/data-usage).
### Text detection
Google can extract text from an image and provide it as a string, which can then be used for automatically checking a challenge.
### Face detection
Faces can be detected, as well as where the faces are located in the picture.
### Landmark detection
Google can detect famous landmarks in pictures and provide us with the name, location and where in the picture the landmark is visible. This can be used to automatically check for a location.
A landmark will usually be detected if it can be found on Google Maps, with sufficient images and when it is marked as a landmark. These often have a separate image section with the name of the landmark.
### Web detection
Google will check if this image can be found anywhere else on the internet (that is crawled by Google). It will find partial and full matches, providing a URL and the title of the webpage.
### Cost
| Feature            | First 1000 tests per month | 1001 - 5.000.000 tests per month per 1000 tests | 5.000.001 and higher per month per 1000 tests |
|--------------------|----------------------------|-------------------------------------------------|-----------------------------------------------|
| Text Detection     | Free                       | $1.50                                           | $0.60                                         |
| Face Detection     | Free                       | $1.50                                           | $0.60                                         |
| Landmark Detection | Free                       | $1.50                                           | $0.60                                         |
| Web Detection      | Free                       | $3.50                                           | Requires a quote from Google                  |
| **Total**          | **$0.00**                  | **$8.00 per 1000 tests**                        | **$5.30 per 1000 tests (worst case)**         |

# Integrating with a website

The Bouncer web app does not include a customer portal, for customers who want to be reviewed. This must be handled by the backend of the advertising platform. This page contains documentation on how to integrate Bouncer into your existing backend flows.

# API keys
## Managing keys
Interacting with the Bouncer API requires API keys. Each platform has it's own set of API keys.
An API key will automatically be generated when a new platform is created by the superuser.

The superuser can generate a new API key when needed by clicking on `reset API key` on the platform page.
The API key for each platform can be viewed by the superuser, by clicking on `view API key` on the platform page.


## Using keys
Logging into the API with the API key is done the OAuth way.
1. Make a POST request to `/auth/api`.

**Body:**
```json
{
    "apiKey": "apiKey"
}
```
**Response:**
```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJmYzgzYzZlZC0zOTFkLThjYTMtOTk2Ny1mZDY0NDczOWJiOGIiLCJhdWQiOiJib3VuY2VyLWNsaWVudCIsInJvbGUiOiJQTEFURk9STSBmYzgzYzZlZC0zOTFkLThjYTMtOTk2Ny1mZDY0NDczOWJiOGIiLCJpc3MiOiJib3VuY2VyLWFwaSIsImV4cCI6MTY0NDAyNDc4NiwiaWF0IjoxNjQ0MDAzMTg2fQ.OJO_Dz7mwKA87vLI25JMZTehqnhVidzqIB5_w6wO6gc"
}
```

2. The received JWT token can then be send as Bearer token with each request.
   The header looks like this: `Authentication: Bearer <JWT>`

3. After the token has expired, the API will return a 403 or 401. When this occurs, a new token should be requested using the API key.

# Verifications
Each time someone needs to be verified a verification needs to be opened. We advise the name of this verification to be the username or ID of the user that is up for verification.

## Creating verifications
1. Make sure you are sending a valid JWT as Bearer authentication.
2. Make a POST request to `/verification`.

**Body:**
```json
{
    "name": "usernameOrIdToBeVerified"
}
```
**Response:**
```json
{
    "id": "f881ca14-171e-4b78-a10a-db97f7f9e52c",
    "createDate": "2022-01-26T18:40:34.158+00:00",
    "status": "OPEN",
    "name": "usernameOrIdToBeVerified",
    "maxChallengeLifetime": 5
}
```

3. Use the response to give feedback to the user.

## Listing verifications
1. Make sure you are sending a valid JWT as Bearer authentication.
2. Make a GET request to `/verification` use `?size=10&page=0` URL parameters to paginate the list.

<details>
  <summary><b>Click to view response</b></summary>

  ```json
{
    "content": [
        {
            "id": "f881ca14-171e-4b78-a10a-db97f7f9e52c",
            "createDate": "2022-01-26T18:40:34.158+00:00",
            "status": "OPEN",
            "name": "usernameOrIdToBeVerified",
            "maxChallengeLifetime": 5 
        }
    ],
    "pageable": {
        "sort": {
            "empty": false,
            "sorted": true,
            "unsorted": false
        },
        "offset": 0,
        "pageNumber": 0,
        "pageSize": 10,
        "paged": true,
        "unpaged": false
    },
    "last": true,
    "totalElements": 1,
    "totalPages": 1,
    "size": 10,
    "number": 0,
    "sort": {
        "empty": false,
        "sorted": true,
        "unsorted": false
    },
    "first": true,
    "numberOfElements": 1,
    "empty": false
}
```
</details>

3. Use the response located in `content`. Or retrieve more pages using the pagination metadata in `pageable`.

# Challenges
Verifications contain challenges. By default each opened verification has one challenge, this challenge contains the image that will be used in the advertisement.
## Listing challenges
1. Make sure you are sending a valid JWT as Bearer authentication.
2. Make a GET request to `/verification/<verification-id>/challenge` use `?size=10&page=0` URL parameters to paginate the list.

<details>
  <summary><b>Click to view response</b></summary>

  ```json
{
    "content": [
        {
            "createDate": "2022-01-26T18:40:34.202+00:00",
            "expiryDate": "2022-01-31T18:40:34.202+00:00",
            "status": "REJECTED",
            "text": "Submit the image of your advertisement",
            "useLandmarkDetection": false,
            "landmarkMatch": "",
            "useTextDetection": false,
            "textMatch": "",
            "useFaceDetection": false,
            "useWebDetection": false,
            "verdict": {
                "id": "6ef2c2f5-4ec7-44f5-905f-35bc8678b080",
                "approved": false,
                "reason": "Some reason for denial"
            },
            "useCoordinateMatching": false,
            "longitude": 0.0,
            "latitude": 0.0,
            "maxRange": 0.0,
            "id": "64a2e9f1-f922-4dad-b93e-bcc78dc4866d"
        }
    ],
    "pageable": {
        "sort": {
            "empty": false,
            "sorted": true,
            "unsorted": false
        },
        "offset": 0,
        "pageNumber": 0,
        "pageSize": 10,
        "paged": true,
        "unpaged": false
    },
    "last": true,
    "totalElements": 1,
    "totalPages": 1,
    "size": 10,
    "number": 0,
    "sort": {
        "empty": false,
        "sorted": true,
        "unsorted": false
    },
    "first": true,
    "numberOfElements": 1,
    "empty": false
}
```
</details>

3. Use the response located in `content`. Or retrieve more pages using the pagination metadata in `pageable`.

# Images
## Upload image to a challenge
When creating a new verification, the first challenge is automatically created. This challenge contains the advertising image.
Your back-end can upload images to challenges. Each challenge can have one image associated to it.

1. Make sure you are sending a valid JWT as Bearer authentication.
2. Retrieve the verification and challenge ID to upload the image to.
3. Make a POST request to /verification/<verification-id>/challenge/<challenge-id>/media`
4. As body send form data, this form data should contain one entry: `file`. Which contains a blob (the image).
5. Use the response to notify the user and to display the data that was extracted from their image.

<details>
  <summary><b>Click to view response</b></summary>

This response was created by submitting a deepfake image of Queen Elizabeth.
```json
{
	"id": "122ff244-f079-4f4d-8f61-33796821a0db",
	"size": 219024,
	"originalName": "1800.jpg",
	"filePath": "b8c8d5c2-0879-4419-9d79-6e798a4699d6",
	"contentType": "image/jpeg",
	"extractedText": "",
	"labels": "deepfake queen",
	"faceCount": 1,
	"exif": {
		"id": "1152f54b-ba2b-49db-9900-d83ee8e9bd41",
		"gpsLat": "Unknown",
		"gpsLatRef": "Unknown",
		"gpsLong": "Unknown",
		"gpsLongRef": "Unknown",
		"phoneModel": "Unknown",
		"originalDateString": "Unknown",
		"gpsDateString": "Unknown",
		"lastModifiedDateString": "Unknown",
		"flash": "Unknown",
		"originalDate": null,
		"lastModifiedDate": null,
		"gpsDate": null
	},
	"faces": [
		{
			"id": "36874283-9aee-4dc7-83dd-76eed6fa2583",
			"vertexes": [
				{
					"id": "4c2e5168-9ff5-496d-9ead-9b7d2d5c40ea",
					"x": 546.0,
					"y": 278.0
				},
				{
					"id": "f7a11763-e479-410c-952b-0b2aa176a5dd",
					"x": 546.0,
					"y": 388.0
				},
				{
					"id": "24e40b2b-c8cd-47c8-8913-cdb6a46c1ea1",
					"x": 641.0,
					"y": 388.0
				},
				{
					"id": "2f2b5120-d5f8-47b7-986f-060e61eec85b",
					"x": 641.0,
					"y": 278.0
				}
			]
		}
	],
	"landmarks": [],
	"webPageMatches": [
		{
			"id": "9317d1fc-aa45-450f-9d6a-58a56e88563b",
			"title": "Channel 4 creates &#39;<b>deepfake</b>&#39; <b>Queen</b> for alternative Christmas ...",
			"url": "https://www.independent.co.uk/news/uk/home-news/queen-deepfake-channel-4-christmas-message-b1778542.html",
			"fullMatches": 1,
			"partialMatches": 0
		}
	],
	"visionCheckFailures": [
		{
			"id": "8dcbc655-5878-4018-9069-4ce1c9970116",
			"reason": "This image was found on the internet 1 time.",
			"type": "WEB_DETECTION"
		},
		{
			"id": "646e2c3b-f9f4-4cf3-a0ef-2657a9106fdf",
			"reason": "The submitted image text does not match: 'Plopperdeplop'.",
			"type": "TEXT_DETECTION"
		}
	]
}
```
</details>

<details>
  <summary><b>Click to view example in Angular</b></summary>

**HTML template**
```html
<input type="file" (change)="fileInputChange($event)" accept=".jpg,.png,.jpeg"/>
```

**Typescript template**
```typescript
public fileInputChange(fileInputEvent): void {
    const file: File = fileInputEvent.target.files[0];
    this.uploadService.uploadImage(file).subscribe(media => this.mediaResponse = media);
}	
```

**Upload service**
```typescript
public uploadImage(file: File): Observable<MediaModel> {
    const formData: FormData = new FormData();
    formData.append("file", file, file.name);
    return this.http.post<MediaModel>(`${apiUrl}/verification/${verificationId}/challenge/${challengeId}/media`, formData);
}		
```
</details>

## Listing all images for a challenge
1. Make sure you are sending a valid JWT as Bearer authentication.
2. Make a GET request to `/verification/<verification-id>/challenge/<challenge-id>/media` use `?size=10&page=0` URL parameters to paginate the list.
3. Show users which images they have submitted. The media response can be found in the 'Upload an image' section. The only difference is that it will this time be contained in a paginated response. With `content` and `pageable` sections.

## Deleting an image
1. Make sure you are sending a valid JWT as Bearer authentication.
2. Make a DELETE request to `/verification/<verification-id>/challenge/<challenge-id>/media/<media-id>`.
3. The image has now been deleted from the database and file storage. Confirmed by a `204 no-content` empty response.

# Webhooks
In the sections above we have learned how to send requests to the Bouncer API. But what if we want to notify users when a new challenge is created. Or when they have been accepted/rejected. For this we would need the Bouncer API to send a request to your own back-end.

Bouncer API supports this in the form of webhooks. The admin for your platform can add webhooks. Adding a webhook requires a URL to send it to, a secret and what to listen for.

When a webhook is triggered (a challenge is created for example) the Bouncer API will send a request to the specified URL. This request contains the new, updated or removed data. So you can act on this and notify your users.

The secret is there to verify if the request actually came from the Bouncer API. By checking for the secret that only the Bouncer API and your back-end know, we can deny other malicious requests.

### Possible entity types
- CHALLENGE
- VERDICT
- VERIFICATION

### Possible types
- CREATE
- UPDATE
- DELETE

### Example response when a new challenge is created
```json
{
  "secret": "iamverysecret",
  "entityType": "CHALLENGE",
  "type": "CREATE",
  "entity": {
    "createDate": 1644006725560,
    "expiryDate": 1644438725560,
    "status": "OPEN",
    "text": "Submit an image where the text 'Amethyst' is visible.",
    "useLandmarkDetection": false,
    "landmarkMatch": "",
    "useTextDetection": true,
    "textMatch": "Amethyst",
    "useFaceDetection": true,
    "useWebDetection": true,
    "verdict": null,
    "useCoordinateMatching": false,
    "longitude": 0,
    "latitude": 0,
    "maxRange": 0,
    "id": "a4419915-50aa-43c8-9934-b8c6063a0b49"
  }
}
```

#Roles

## Superuser
The superuser creates platforms. A platform comes with an admin account, the superuser defines the username and password of the admin.
A platform is entirely separated from another. A platform can be seen as another website using the service. While the superuser can be seen as the owner of the service (CKM).

It is however possible to let a website host their own Bouncer service. In this case they would be the superuser and there would be one platform. Essentially cutting out het CKM.

Responsibilities of the superuser:
- Create platforms with admins

## Admin
The admin manages the associated platform and its users.

Responsibilities of the admin:
- Create new moderators
- Create and maintain presets
- Create and maintain predefs
- Maintain the integration with their website using webhooks
- Set sensible challenge lifetime settings
- May delete the entire platform if required


## Moderator
The moderator is the workhorse of the program, it verifies users for their website.
Note: The admin can not see any verifications or such, this is to keep responsibilities and influence separate.

Responsibilities of the moderator:
- View verifications
- Reject or accept a verification
- View challenges
- View submitted images
- Reject or accept a verification
- Create new challenges from presets

# Setting up Bouncer

## Prerequisites
In order for this app to work properly and to its full extent it must be linked to prerequisite services.
### MySQL 8 Database
The API requires a MySQL 8 database in order to store its data.
A DB can be hosted by a cloud provider such as Digital Ocean, or it can be set up yourself.
### Google Cloud API
#### Vision AI API
The backend needs [Vision AI](https://github.com/jely2002/IPSEN3-CKM-FRONT/wiki/Image-data-processing#google-cloud-vision-ai) to extract additional data from images.
Vision AI is part of the Google Cloud platform.

[Select get started on this page to generate keys and set up a service account.](https://cloud.google.com/vision)

When keys have been generated they can be downloaded as JSON. And then encoded in base64, this can then be entered in the environment variables.
#### Google Maps API
The frontend needs a public Google Maps API key, so it can display location data on a map.

[Follow these instructions](https://developers.google.com/maps/documentation/javascript/cloud-setup) on how to generate an API key for the Google Maps Javascript API.

Make sure to restrict the API key to only the domain name that the web app is running on, so it cannot be abused.

After receiving an API key it can be added to `environment.ts` and `environment.prod.ts`.
### S3 Storage (optional)
S3 storage is an optional storage mode, for when storing images on the same server is not desired. We recommend S3 be used, for better separation of data and enhanced reliability / availability.

In theory this API is compatible with [every S3 compatible cloud storage provider](https://help.servmask.com/knowledgebase/list-of-s3-compatible-storage-providers/).
## Setting environment variables
The backend environment variables can be set in a .env file at the root of the project. When running with docker they have to be set individually.

A .env.example file can be found in the root of the backend project, to use as a template.

With docker compose the .env file can be read.

| Environment Variable      | Example value                             | Description                                                                                            |
|---------------------------|-------------------------------------------|--------------------------------------------------------------------------------------------------------|
| ENVIRONMENT               | production                                | Setting this to develop makes it easier to debug and test certain features locally.                    |
| BOUNCER_DATABASE_HOST     | bouncer-mysql                             | The hostname where the MYSQL database of the bouncer service can be reached. Without protocol or port. |
| BOUNCER_DATABASE_NAME     | bouncer                                   | The database name to use.                                                                              |
| BOUNCER_DATABASE_PORT     | 3306                                      | The port that the MySQL instance for bouncer service can be reached at                                 |
| PRESET_DATABASE_HOST      | preset-mysql                              | The hostname where the MYSQL database of the preset service can be reached. Without protocol or port.  |
| PRESET_DATABASE_NAME      | preset                                    | The database name to use.                                                                              |
| PRESET_DATABASE_PORT      | 3307                                      | The port that the MySQL instance for preset service can be reached at                                  |
| WEBHOOK_DATABASE_HOST     | webhook-mysql                             | The hostname where the MYSQL database of the webhook service can be reached. Without protocol or port. |
| WEBHOOK_DATABASE_NAME     | webhook                                   | The database name to use.                                                                              |
| WEBHOOK_DATABASE_PORT     | 3308                                      | The port that the MySQL instance for webhook service can be reached at                                 |                                                                     |
| DATABASE_USER             | bouncer-api                               | The username that is used to log in to the database.                                                   |
| DATABASE_PASSWORD         | ***********                               | The password that is used to log in to the database.                                                   |
| MAX_UPLOAD_SIZE_MB        | 50                                        | The maximum size of a file that can be uploaded to the API.                                            |
| MAX_REQUEST_SIZE_MB       | 60                                        | The maximum size of an entire request that can be send. File + request body.                           |
| STORAGE_PATH              | /data                                     | This is where images will be stored when S3 storage is not used.                                       |
| S3_ENDPOINT               | ams3.digitaloceanspaces.com               | The S3 endpoint where your bucket is located.                                                          |
| S3_REGION                 | ams3                                      | The region of your bucket.                                                                             |
| S3_ACCESS_KEY             | **********                                | Access key used to access your bucket.                                                                 |
| S3_SECRET_KEY             | **********                                | Secret key used to access your bucket.                                                                 |
| S3_BUCKET                 | bouncer-api                               | The name of the bucket to use.                                                                         |
| USE_GCP_VISION            | true                                      | Enable or disable Google Cloud Platform Vision AI features.                                            |
| GCP_B64_CREDENTIALS       | **********                                | A base64 encoded JSON file containing your service worker credentials for GCP.                         |
| GCP_PROJECT_ID            | **********                                | The project ID for your service worker, must be the project where Vision AI is on.                     |
| SUPERUSER_USERNAME        | bouncer                                   | The username for the superuser.                                                                        |
| SUPERUSER_HASHED_PASSWORD | **********                                | A password for the superuser, hashed with argon2ID and base64 encoded after.                           |
| MAX_CHALLENGE_LIFETIME    | 5                                         | The default maximum amount of challenges per verification.                                             |
| MAX_DAY_LIFETIME          | 7                                         | The default maximum amount of days before a verification expires.                                      |
| JWT_COOKIE_NAME           | bouncer_access_token                      | The name of the cookie where the authentication JWT is stored in.                                      |
| JWT_LIFETIME              | 21600000                                  | How long a JWT is valid, in milliseconds.                                                              |
| JWT_SECRET                | **********                                | A 128 character long string with random characters, to sign the JWT with.                              |
| JWT_COOKIE_SECURE         | true                                      | Whether the JWT cookie has secure mode enabled. Enable when using HTTPS (a must).                      |
| JWT_COOKIE_RESTRICT_SITE  | true                                      | Whether the JWT cookie is restricted to the domain it was given from. Enable in production.            |
| CORS_ALLOWED_ORIGINS      | http://localhost:4200,https://bouncer.com | Comma separated domains to allow requests from.                                                        |
| WEBHOOK_SERVICE_PORT      | 8084                                      | The port that the webhook service can be reached at                                                    |
| PRESET_SERVICE_PORT       | 8083                                      | The port that the preset service can be reached at                                                     |
| BOUNCER_SERVICE_PORT      | 8081                                      | The port that the bouncer service can be reached at                                                    |
| GATEWAY_SERVICE_PORT      | 8080                                      | The port that the gateway service can be reached at                                                    |

## Starting the application

When deploying the API in production, a docker image should be build and run on the server. The API can be deployed without docker, but it's a lot easier to use Docker.

1. Build a docker image using the Dockerfile.
   1. Change directory to the service that needs to be build.
   2. run `docker build -t <name of the service>:<your environment> .`
2. Log in to a docker registry (optional)
3. Tag the image for pushing to a registry (optional) `docker tag bouncer-api:<your environment> <registry URL>:<your environment>`
4. Push the image to the registry (optional) `docker push <registry URL>:<your environment>`
5. Pull the image from the registry on the server (optional) `docker pull <registry URL>:<your environment>`
6. Create a .env file with environment variables if it doesn't exist yet.
7. Run the image `docker run --env-file .env <registry URL>:<your environment>`

## Securing the application
- The site must be served over HTTPS to ensure passwords are sent encrypted
- Create accounts with secure credentials
- By default images are encrypted before storing them