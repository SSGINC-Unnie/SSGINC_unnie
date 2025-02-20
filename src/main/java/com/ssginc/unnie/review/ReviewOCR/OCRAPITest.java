//package com.ssginc.unnie.review.ReviewOCR;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import java.io.*;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.UUID;
//
//
//public class OCRAPITest {
//
//    public String ocr(String fileName) {
//        String apiURL = NaverOCR.OCR_URL;
//        String secretKey = NaverOCR.SECRECT_KEY;
//        String imageFile = "C:\\workSpace\\SSGINC_Unnie\\src\\main\\resources\\static\\img\\Receipt2.jpg";
//
//        StringBuilder result = new StringBuilder();
//
//        try {
//            URL url = new URL(apiURL);
//            HttpURLConnection con = (HttpURLConnection) url.openConnection();
//            con.setUseCaches(false);
//            con.setDoInput(true);
//            con.setDoOutput(true);
//            con.setReadTimeout(30000);
//            con.setRequestMethod("POST");
//            String boundary = "----" + UUID.randomUUID().toString().replaceAll("-", "");
//            con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
//            con.setRequestProperty("X-OCR-SECRET", secretKey);
//
//            JSONObject json = new JSONObject();
//            json.put("version", "V2");
//            json.put("requestId", UUID.randomUUID().toString());
//            json.put("timestamp", System.currentTimeMillis());
//            JSONObject image = new JSONObject();
//            image.put("format", "jpg");
//            image.put("name", "Receipt");
//            JSONArray images = new JSONArray();
//            images.put(image);
//            json.put("images", images);
//            String postParams = json.toString();
//
//            con.connect();
//            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
//            long start = System.currentTimeMillis();
//            File file = new File(imageFile);
//            writeMultiPart(wr, postParams, file, boundary);
//            wr.close();
//
//            int responseCode = con.getResponseCode();
//            BufferedReader br;
//            if (responseCode == 200) {
//                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
//            } else {
//                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
//            }
//            String inputLine;
//            StringBuffer response = new StringBuffer();
//            while ((inputLine = br.readLine()) != null) {
//                response.append(inputLine);
//            }
//            br.close();
////
//            JSONObject obj = new JSONObject(response.toString());
//
//            //1. images배열로 추출
//            JSONArray imgArray = obj.getJSONArray("images");
//
//            //2. 배열안에 index가 한개만 있음. 한개만 추출
//            JSONObject img = imgArray.getJSONObject(0);
//
//            //3. fields배열로 추출
//            JSONArray fields = img.getJSONArray("fields");
//
//            //4. 배열 반복문으로 해서 추출
//            for (int i = 0; i < fields.length(); i++) {
//                JSONObject field = fields.getJSONObject(i);
//                String inferText = field.getString("inferText");
//                System.out.println(i + ">> inferText: " + inferText);
//                result.append(inferText + " ");
//            }
//        } catch (Exception e) {
//            System.out.println(e);
//        }
//        System.out.println("=====================================");
//        System.out.println("result: " + result);
//        return result.toString();
//    }
//
//    private static void writeMultiPart(OutputStream out, String jsonMessage, File file, String boundary) throws
//            IOException {
//        StringBuilder sb = new StringBuilder();
//        sb.append("--").append(boundary).append("\r\n");
//        sb.append("Content-Disposition:form-data; name=\"message\"\r\n\r\n");
//        sb.append(jsonMessage);
//        sb.append("\r\n");
//
//        out.write(sb.toString().getBytes("UTF-8"));
//        out.flush();
//
//        if (file != null && file.isFile()) {
//            out.write(("--" + boundary + "\r\n").getBytes("UTF-8"));
//            StringBuilder fileString = new StringBuilder();
//            fileString
//                    .append("Content-Disposition:form-data; name=\"file\"; filename=");
//            fileString.append("\"" + file.getName() + "\"\r\n");
//            fileString.append("Content-Type: application/octet-stream\r\n\r\n");
//            out.write(fileString.toString().getBytes("UTF-8"));
//            out.flush();
//
//            try (FileInputStream fis = new FileInputStream(file)) {
//                byte[] buffer = new byte[8192];
//                int count;
//                while ((count = fis.read(buffer)) != -1) {
//                    out.write(buffer, 0, count);
//                }
//                out.write("\r\n".getBytes());
//            }
//
//            out.write(("--" + boundary + "--\r\n").getBytes("UTF-8"));
//        }
//        out.flush();
//    }
//}