/*
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.huawei.hms.fido.sample.fido2.server;

import android.util.Log;

import com.huawei.hms.fido.sample.fido2.server.param.ServerAssertionResultRequest;
import com.huawei.hms.fido.sample.fido2.server.param.ServerAssertionResultResponseRequest;
import com.huawei.hms.fido.sample.fido2.server.param.ServerAttestationResultRequest;
import com.huawei.hms.fido.sample.fido2.server.param.ServerAttestationResultResponseRequest;
import com.huawei.hms.fido.sample.fido2.server.param.ServerAuthenticatorSelectionCriteria;
import com.huawei.hms.fido.sample.fido2.server.param.ServerPublicKeyCredentialCreationOptionsResponse;
import com.huawei.hms.fido.sample.fido2.server.param.ServerPublicKeyCredentialDescriptor;
import com.huawei.hms.fido.sample.fido2.server.param.ServerPublicKeyCredentialParameters;
import com.huawei.hms.support.api.fido.fido2.Algorithm;
import com.huawei.hms.support.api.fido.fido2.Attachment;
import com.huawei.hms.support.api.fido.fido2.AttestationConveyancePreference;
import com.huawei.hms.support.api.fido.fido2.AuthenticatorAssertionResponse;
import com.huawei.hms.support.api.fido.fido2.AuthenticatorAttestationResponse;
import com.huawei.hms.support.api.fido.fido2.AuthenticatorMetadata;
import com.huawei.hms.support.api.fido.fido2.AuthenticatorSelectionCriteria;
import com.huawei.hms.support.api.fido.fido2.AuthenticatorTransport;
import com.huawei.hms.support.api.fido.fido2.Fido2Client;
import com.huawei.hms.support.api.fido.fido2.Fido2Extension;
import com.huawei.hms.support.api.fido.fido2.PublicKeyCredentialCreationOptions;
import com.huawei.hms.support.api.fido.fido2.PublicKeyCredentialDescriptor;
import com.huawei.hms.support.api.fido.fido2.PublicKeyCredentialParameters;
import com.huawei.hms.support.api.fido.fido2.PublicKeyCredentialRequestOptions;
import com.huawei.hms.support.api.fido.fido2.PublicKeyCredentialRpEntity;
import com.huawei.hms.support.api.fido.fido2.PublicKeyCredentialType;
import com.huawei.hms.support.api.fido.fido2.PublicKeyCredentialUserEntity;
import com.huawei.hms.support.api.fido.fido2.UserVerificationRequirement;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Server Utilities
 *
 * @author Huawei HMS
 * @since 2020-03-08
 */
public class ServerUtils {
    private static final String TAG = "ServerUtils";

    private ServerUtils() {
    }

    public static PublicKeyCredentialCreationOptions
        convert2PublicKeyCredentialCreationOptions(Fido2Client fido2Client,
            ServerPublicKeyCredentialCreationOptionsResponse response) {
        PublicKeyCredentialCreationOptions.Builder builder = new PublicKeyCredentialCreationOptions.Builder();

        String name = response.getRp().getName();
        PublicKeyCredentialRpEntity entity = new PublicKeyCredentialRpEntity(name, name, null);
        builder.setRp(entity);

        String id = response.getUser().getId();
        try {
            builder.setUser(new PublicKeyCredentialUserEntity(id, id.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        builder.setChallenge(ByteUtils.base642Byte(response.getChallenge()));

        if (response.getPubKeyCredParams() != null) {
            List<PublicKeyCredentialParameters> parameters = new ArrayList<>();
            ServerPublicKeyCredentialParameters[] serverPublicKeyCredentialParameters = response.getPubKeyCredParams();
            for (ServerPublicKeyCredentialParameters param : serverPublicKeyCredentialParameters) {
                try {
                    PublicKeyCredentialParameters parameter = new PublicKeyCredentialParameters(
                        PublicKeyCredentialType.PUBLIC_KEY, Algorithm.fromCode(param.getAlg()));
                    parameters.add(parameter);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
            builder.setPubKeyCredParams(parameters);
        }

        if (response.getExcludeCredentials() != null) {
            List<PublicKeyCredentialDescriptor> descriptors = new ArrayList<>();
            ServerPublicKeyCredentialDescriptor[] serverDescriptors = response.getExcludeCredentials();
            for (ServerPublicKeyCredentialDescriptor desc : serverDescriptors) {
                ArrayList<AuthenticatorTransport> transports = new ArrayList<>();
                if (desc.getTransports() != null) {
                    try {
                        transports.add(AuthenticatorTransport.fromValue(desc.getTransports()));
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                }
                PublicKeyCredentialDescriptor descriptor = new PublicKeyCredentialDescriptor(
                    PublicKeyCredentialType.PUBLIC_KEY, ByteUtils.base642Byte(desc.getId()), transports);
                descriptors.add(descriptor);
            }
            builder.setExcludeList(descriptors);
        }

        Attachment attachment = null;
        if (response.getAuthenticatorSelection() != null) {
            ServerAuthenticatorSelectionCriteria selectionCriteria = response.getAuthenticatorSelection();
            if (selectionCriteria.getAuthenticatorAttachment() != null) {
                try {
                    attachment = Attachment.fromValue(selectionCriteria.getAuthenticatorAttachment());
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            Boolean residentKey = selectionCriteria.isRequireResidentKey();

            UserVerificationRequirement requirement = null;
            if (selectionCriteria.getUserVerification() != null) {
                try {
                    requirement = UserVerificationRequirement.fromValue(selectionCriteria.getUserVerification());
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            com.huawei.hms.support.api.fido.fido2.AuthenticatorSelectionCriteria fido2Selection =
                new AuthenticatorSelectionCriteria(attachment, residentKey, requirement);
            builder.setAuthenticatorSelection(fido2Selection);
        }

        // attestation
        if (response.getAttestation() != null) {
            try {
                AttestationConveyancePreference preference =
                    AttestationConveyancePreference.fromValue(response.getAttestation());
                builder.setAttestation(preference);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }

        HashMap<String, Object> extensions = new HashMap<>();
        if (response.getExtensions() != null) {
            extensions.putAll(response.getExtensions());
        }

        // Specify a platform authenticator and related extension items. You can specify a platform
        // authenticator or not as needed.
        if( Attachment.PLATFORM.equals(attachment) ){
            useSelectedPlatformAuthenticator(fido2Client,extensions);
        }
        builder.setExtensions(extensions);


        builder.setTimeoutSeconds(response.getTimeout());
        return builder.build();
    }

    public static PublicKeyCredentialRequestOptions
        convert2PublicKeyCredentialRequestOptions(Fido2Client fido2Client,
                ServerPublicKeyCredentialCreationOptionsResponse response,
                boolean isUseSelectedPlatformAuthenticator) {
        PublicKeyCredentialRequestOptions.Builder builder = new PublicKeyCredentialRequestOptions.Builder();

        builder.setRpId(response.getRpId());

        builder.setChallenge(ByteUtils.base642Byte(response.getChallenge()));

        ServerPublicKeyCredentialDescriptor[] descriptors = response.getAllowCredentials();
        if (descriptors != null) {
            List<PublicKeyCredentialDescriptor> descriptorList = new ArrayList<>();
            for (ServerPublicKeyCredentialDescriptor descriptor : descriptors) {
                ArrayList<AuthenticatorTransport> transports = new ArrayList<>();
                if (descriptor.getTransports() != null) {
                    try {
                        transports.add(AuthenticatorTransport.fromValue(descriptor.getTransports()));
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                }
                PublicKeyCredentialDescriptor desc = new PublicKeyCredentialDescriptor(
                    PublicKeyCredentialType.PUBLIC_KEY, ByteUtils.base642Byte(descriptor.getId()), transports);
                descriptorList.add(desc);
            }
            builder.setAllowList(descriptorList);
        }

        HashMap<String, Object> extensions = new HashMap<>();
        if (response.getExtensions() != null) {
            extensions.putAll(response.getExtensions());
        }
        // Specify a platform authenticator and related extension items. You can specify a platform
        // authenticator or not as needed.
        if( isUseSelectedPlatformAuthenticator ){
            useSelectedPlatformAuthenticator(fido2Client,extensions);
        }
        builder.setExtensions(extensions);
        builder.setTimeoutSeconds(response.getTimeout());
        return builder.build();
    }

    public static ServerAttestationResultRequest
        convert2ServerAttestationResultRequest(AuthenticatorAttestationResponse authenticatorAttestationResponse) {
        ServerAttestationResultRequest request = new ServerAttestationResultRequest();
        ServerAttestationResultResponseRequest attestationResponse = new ServerAttestationResultResponseRequest();
        attestationResponse
            .setAttestationObject(ByteUtils.byte2base64(authenticatorAttestationResponse.getAttestationObject()));
        attestationResponse
            .setClientDataJSON(ByteUtils.byte2base64(authenticatorAttestationResponse.getClientDataJson()));
        request.setResponse(attestationResponse);
        request.setId(ByteUtils.byte2base64(authenticatorAttestationResponse.getCredentialId()));
        request.setType("public-key");
        return request;
    }

    public static ServerAssertionResultRequest
        convert2ServerAssertionResultRequest(AuthenticatorAssertionResponse authenticatorAssertation) {
        ServerAssertionResultResponseRequest assertionResultResponse = new ServerAssertionResultResponseRequest();
        assertionResultResponse.setSignature(ByteUtils.byte2base64(authenticatorAssertation.getSignature()));
        assertionResultResponse.setClientDataJSON(ByteUtils.byte2base64(authenticatorAssertation.getClientDataJson()));
        assertionResultResponse
            .setAuthenticatorData(ByteUtils.byte2base64(authenticatorAssertation.getAuthenticatorData()));

        ServerAssertionResultRequest request = new ServerAssertionResultRequest();
        request.setResponse(assertionResultResponse);

        request.setId(ByteUtils.byte2base64(authenticatorAssertation.getCredentialId()));

        request.setType("public-key");
        return request;
    }

    // Specify a platform authenticator and related extension items.
    private static void useSelectedPlatformAuthenticator(Fido2Client fido2Client,
            HashMap<String, Object> extensions) {
        if (!fido2Client.hasPlatformAuthenticators()) {
            return;
        }
        List<String> selectedAuthenticatorList = new ArrayList<>();
        for (AuthenticatorMetadata meta : fido2Client.getPlatformAuthenticators()) {
            if( !meta.isAvailable() ){
                continue;
            }
            // Fingerprint authenticator
            if (meta.isSupportedUvm(AuthenticatorMetadata.UVM_FINGERPRINT)) {
                selectedAuthenticatorList.add(meta.getAaguid());

                if( meta.getExtensions().contains(Fido2Extension.W3C_WEBAUTHN_UVI.getIdentifier())){
                    // Indicates whether to verify the fingerprint ID. If the value is true, the
                    // same finger must be used for both registration and verification.
                    extensions.put(Fido2Extension.W3C_WEBAUTHN_UVI.getIdentifier(), Boolean.TRUE);
                }

                if( meta.getExtensions().contains(Fido2Extension.HMS_R_PA_CIBBE_01.getIdentifier())){
                    // Indicates whether the authentication credential expires when the biometric
                    // feature changes. If the value is true, the key will expire when the fingerprint
                    // is enrolled. This is valid only for registration.
                    extensions.put(Fido2Extension.HMS_R_PA_CIBBE_01.getIdentifier(), Boolean.TRUE);
                }
            }
            // Lock screen 3D face authenticator
            else if (meta.isSupportedUvm(AuthenticatorMetadata.UVM_FACEPRINT)) {
            // selectedAuthenticatorList.add(meta.getAaguid());
                Log.i(TAG, "Lock screen 3D face authenticator");
            }
        }
        extensions.put(Fido2Extension.HMS_RA_C_PACL_01.getIdentifier(), selectedAuthenticatorList);
    }
}
