// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.projectoxford.face;

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

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public interface IFaceServiceClient {

    public Face[] detect(String url, boolean analyzesFacialLandmarks, boolean analyzesAge, boolean analyzesGender, boolean analyzesHeadPose) throws ClientException;

    public Face[] detect(InputStream image, boolean analyzesFacialLandmarks, boolean analyzesAge, boolean analyzesGender, boolean analyzesHeadPose) throws ClientException, IOException;

    public VerifyResult verify(UUID faceId1, UUID faceId2) throws ClientException;

    public IdentifyResult[] identify(String personGroupId, UUID[] faceIds, int maxNumOfCandidatesReturned) throws ClientException;

    public TrainingStatus trainPersonGroup(String personGroupId) throws ClientException;

    public TrainingStatus getPersonGroupTrainingStatus(String personGroupId) throws ClientException;

    public void createPersonGroup(String personGroupId, String name, String userData) throws ClientException;

    public void deletePersonGroup(String personGroupId) throws ClientException;

    public void updatePersonGroup(String personGroupId, String name, String userData) throws ClientException;

    public PersonGroup getPersonGroup(String personGroupId) throws ClientException;

    public PersonGroup[] getPersonGroups() throws ClientException;

    public CreatePersonResult createPerson(String personGroupId, UUID[] faceIds, String name, String userData) throws ClientException;

    public Person getPerson(String personGroupId, UUID personId) throws ClientException;

    public Person[] getPersons(String personGroupId) throws ClientException;

    public void addPersonFace(String personGroupId, UUID personId, UUID faceId, String userData) throws ClientException;

    public PersonFace getPersonFace(String personGroupId, UUID personId, UUID faceId) throws ClientException;

    public void updatePersonFace(String personGroupId, UUID personId, UUID faceId, String userData) throws ClientException;

    public void updatePerson(String personGroupId, UUID personId, UUID[] faceIds, String name, String userData) throws ClientException;

    public void deletePerson(String personGroupId, UUID personId) throws ClientException;

    public void deletePersonFace(String personGroupId, UUID personId, UUID faceId) throws ClientException;

    public SimilarFace[] findSimilar(UUID faceId, UUID[] faceIds) throws ClientException;

    public GroupResult group(UUID[] faceIds) throws ClientException;
}
