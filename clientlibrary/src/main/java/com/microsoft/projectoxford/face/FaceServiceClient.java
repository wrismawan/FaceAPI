// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.projectoxford.face;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.microsoft.projectoxford.face.contract.CreatePersonResult;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.GroupResult;
import com.microsoft.projectoxford.face.contract.IdentifyResult;
import com.microsoft.projectoxford.face.contract.Person;
import com.microsoft.projectoxford.face.contract.PersonFace;
import com.microsoft.projectoxford.face.contract.PersonGroup;
import com.microsoft.projectoxford.face.contract.SimilarFace;
import com.microsoft.projectoxford.face.contract.TrainingStatus;
import com.microsoft.projectoxford.face.contract.VerifyResult;
import com.microsoft.projectoxford.face.rest.ClientException;
import com.microsoft.projectoxford.face.rest.WebServiceRequest;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FaceServiceClient implements IFaceServiceClient {
    private static final String ServiceHost = "https://api.projectoxford.ai/face/v0";
    private WebServiceRequest restCall = null;
    private Gson gson = new Gson();

    public FaceServiceClient(String subscriptKey) {
        this.restCall = new WebServiceRequest(subscriptKey);
    }

    @Override
    public Face[] detect(String url, boolean analyzesFacialLandmarks, boolean analyzesAge, boolean analyzesGender, boolean analyzesHeadPose) throws ClientException {
        Map<String, Object> params = new HashMap<>();

        params.put("analyzesFacialLandmarks", analyzesFacialLandmarks);
        params.put("analyzesAge", analyzesAge);
        params.put("analyzesGender", analyzesGender);
        params.put("analyzesHeadPose", analyzesHeadPose);

        String path = ServiceHost + "/detections";
        String uri = WebServiceRequest.getUrl(path, params);

        params.clear();
        params.put("url", url);

        String json = this.restCall.request(uri, "POST", params, null);
        Type listType = new TypeToken<List<Face>>() {
        }.getType();
        List<Face> faces = this.gson.fromJson(json, listType);

        return faces.toArray(new Face[faces.size()]);
    }

    @Override
    public Face[] detect(InputStream image, boolean analyzesFacialLandmarks, boolean analyzesAge, boolean analyzesGender, boolean analyzesHeadPose) throws ClientException, IOException {
        Map<String, Object> params = new HashMap<>();

        params.put("analyzesAge", analyzesAge);
        params.put("analyzesGender", analyzesGender);
        params.put("analyzesFacialLandmarks", analyzesFacialLandmarks);
        params.put("analyzesHeadPose", analyzesHeadPose);

        String path = ServiceHost + "/detections";
        String uri = WebServiceRequest.getUrl(path, params);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int bytesRead;
        byte[] bytes = new byte[1024];
        while ((bytesRead = image.read(bytes)) > 0) {
            byteArrayOutputStream.write(bytes, 0, bytesRead);
        }
        byte[] data = byteArrayOutputStream.toByteArray();
        params.clear();
        params.put("data", data);

        String json = this.restCall.request(uri, "POST", params, "application/octet-stream");
        Type listType = new TypeToken<List<Face>>() {
        }.getType();
        List<Face> faces = this.gson.fromJson(json, listType);

        return faces.toArray(new Face[faces.size()]);
    }

    @Override
    public VerifyResult verify(UUID faceId1, UUID faceId2) throws ClientException {
        Map<String, Object> params = new HashMap<>();

        params.put("faceId1", faceId1.toString());
        params.put("faceId2", faceId2.toString());

        String uri = ServiceHost + "/verifications";
        String json = this.restCall.request(uri, "POST", params, null);
        Type listType = new TypeToken<VerifyResult>() {
        }.getType();
        return this.gson.fromJson(json, listType);
    }

    @Override
    public IdentifyResult[] identify(String personGroupId, UUID[] faceIds, int maxNumOfCandidatesReturned) throws ClientException {
        Map<String, Object> params = new HashMap<>();

        params.put("personGroupId", personGroupId);
        JSONArray jsonArray = new JSONArray(Arrays.asList(faceIds));
        params.put("faceIds", jsonArray);
        params.put("maxNumOfCandidatesReturned", maxNumOfCandidatesReturned);

        String uri = ServiceHost + "/identifications";
        String json = this.restCall.request(uri, "POST", params, null);
        Type listType = new TypeToken<List<IdentifyResult>>() {
        }.getType();
        List<IdentifyResult> result = this.gson.fromJson(json, listType);

        return result.toArray(new IdentifyResult[result.size()]);
    }

    @Override
    public TrainingStatus trainPersonGroup(String personGroupId) throws ClientException {
        Map<String, Object> params = new HashMap<>();
        String uri = ServiceHost + "/persongroups/" + personGroupId + "/training";
        String json = this.restCall.request(uri, "POST", params, null);
        return this.gson.fromJson(json, TrainingStatus.class);
    }

    @Override
    public TrainingStatus getPersonGroupTrainingStatus(String personGroupId) throws ClientException {
        Map<String, Object> params = new HashMap<>();
        String uri = ServiceHost + "/persongroups/" + personGroupId + "/training";
        String json = this.restCall.request(uri, "GET", params, null);
        return this.gson.fromJson(json, TrainingStatus.class);
    }

    @Override
    public void createPersonGroup(String personGroupId, String name, String userData) throws ClientException {
        Map<String, Object> params = new HashMap<>();
        String uri = ServiceHost + "/persongroups/" + personGroupId;
        params.put("name", name);
        params.put("userData", userData);
        this.restCall.request(uri, "PUT", params, null);
    }

    @Override
    public void deletePersonGroup(String personGroupId) throws ClientException {
        Map<String, Object> params = new HashMap<>();
        String uri = ServiceHost + "/persongroups/" + personGroupId;
        this.restCall.request(uri, "DELETE", params, null);
    }

    @Override
    public void updatePersonGroup(String personGroupId, String name, String userData) throws ClientException {
        Map<String, Object> params = new HashMap<>();

        String uri = ServiceHost + "/persongroups/" + personGroupId;
        params.put("name", name);
        params.put("userData", userData);

        this.restCall.request(uri, "PATCH", params, null);
    }

    @Override
    public PersonGroup getPersonGroup(String personGroupId) throws ClientException {
        Map<String, Object> params = new HashMap<>();
        String uri = ServiceHost + "/persongroups/" + personGroupId;
        String json = this.restCall.request(uri, "GET", params, null);
        return gson.fromJson(json, PersonGroup.class);
    }

    @Override
    public PersonGroup[] getPersonGroups() throws ClientException {
        Map<String, Object> params = new HashMap<>();
        String uri = ServiceHost + "/persongroups";
        String json = this.restCall.request(uri, "GET", params, null);

        Type listType = new TypeToken<List<PersonGroup>>() {
        }.getType();
        List<PersonGroup> result = this.gson.fromJson(json, listType);

        return result.toArray(new PersonGroup[result.size()]);
    }

    @Override
    public CreatePersonResult createPerson(String personGroupId, UUID[] faceIds, String name, String userData) throws ClientException {
        Map<String, Object> params = new HashMap<>();

        String uri = ServiceHost + "/persongroups/" + personGroupId + "/persons";
        JSONArray jsonArray = new JSONArray(Arrays.asList(faceIds));
        params.put("faceIds", jsonArray);
        params.put("name", name);
        params.put("userData", userData);

        String json = this.restCall.request(uri, "POST", params, null);
        return this.gson.fromJson(json, CreatePersonResult.class);
    }

    @Override
    public Person getPerson(String personGroupId, UUID personId) throws ClientException {
        Map<String, Object> params = new HashMap<>();

        String uri = ServiceHost + "/persongroups/" + personGroupId + "/persons/" + personId;
        String json = this.restCall.request(uri, "GET", params, null);
        return this.gson.fromJson(json, Person.class);
    }

    @Override
    public Person[] getPersons(String personGroupId) throws ClientException {
        Map<String, Object> params = new HashMap<>();

        String uri = ServiceHost + "/persongroups/" + personGroupId + "/persons";
        String json = this.restCall.request(uri, "GET", params, null);
        Type listType = new TypeToken<List<Person>>() {
        }.getType();
        List<Person> result = this.gson.fromJson(json, listType);
        return result.toArray(new Person[result.size()]);
    }

    @Override
    public void addPersonFace(String personGroupId, UUID personId, UUID faceId, String userData) throws ClientException {
        Map<String, Object> params = new HashMap<>();

        String uri = ServiceHost + "/persongroups/" + personGroupId + "/persons/" + personId + "/faces/" + faceId;
        params.put("userData", userData);

        this.restCall.request(uri, "PUT", params, null);
    }

    @Override
    public PersonFace getPersonFace(String personGroupId, UUID personId, UUID faceId) throws ClientException {
        Map<String, Object> params = new HashMap<>();
        String uri = ServiceHost + "/persongroups/" + personGroupId + "/persons/" + personId + "/faces/" + faceId;
        String json = this.restCall.request(uri, "GET", params, null);
        return this.gson.fromJson(json, PersonFace.class);
    }

    @Override
    public void updatePersonFace(String personGroupId, UUID personId, UUID faceId, String userData) throws ClientException {
        Map<String, Object> params = new HashMap<>();
        String uri = ServiceHost + "/persongroups/" + personGroupId + "/persons/" + personId + "/faces/" + faceId;
        params.put("userData", userData);
        this.restCall.request(uri, "PATCH", params, null);
    }

    @Override
    public void updatePerson(String personGroupId, UUID personId, UUID[] faceIds, String name, String userData) throws ClientException {
        Map<String, Object> params = new HashMap<>();
        String uri = ServiceHost + "/persongroups/" + personGroupId + "/persons/" + personId;
        JSONArray jsonArray = new JSONArray(Arrays.asList(faceIds));
        params.put("faceIds", jsonArray);
        params.put("name", name);
        params.put("userData", userData);
        this.restCall.request(uri, "PATCH", params, null);
    }

    @Override
    public void deletePerson(String personGroupId, UUID personId) throws ClientException {
        Map<String, Object> params = new HashMap<>();
        String uri = ServiceHost + "/persongroups/" + personGroupId + "/persons/" + personId;
        this.restCall.request(uri, "DELETE", params, null);
    }

    @Override
    public void deletePersonFace(String personGroupId, UUID personId, UUID faceId) throws ClientException {
        Map<String, Object> params = new HashMap<>();
        String uri = ServiceHost + "/persongroups/" + personGroupId + "/persons/" + personId + "/faces/" + faceId;
        this.restCall.request(uri, "DELETE", params, null);
    }

    @Override
    public SimilarFace[] findSimilar(UUID faceId, UUID[] faceIds) throws ClientException {
        Map<String, Object> params = new HashMap<>();
        String uri = ServiceHost + "/findsimilars";
        params.put("faceId", faceId);
        JSONArray jsonArray = new JSONArray(Arrays.asList(faceIds));
        params.put("faceIds", jsonArray);
        String json = this.restCall.request(uri, "POST", params, null);
        Type listType = new TypeToken<List<SimilarFace>>() {
        }.getType();
        List<SimilarFace> result = this.gson.fromJson(json, listType);
        return result.toArray(new SimilarFace[result.size()]);
    }

    @Override
    public GroupResult group(UUID[] faceIds) throws ClientException {
        Map<String, Object> params = new HashMap<>();
        String uri = ServiceHost + "/groupings";
        JSONArray jsonArray = new JSONArray(Arrays.asList(faceIds));
        params.put("faceIds", jsonArray);
        String json = this.restCall.request(uri, "POST", params, null);
        return this.gson.fromJson(json, GroupResult.class);
    }
}
