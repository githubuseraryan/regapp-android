package appcom.example.regsplashscreen.model;

public class DocumentDetails {

    private String docName;
    private String docId;
    private String docEncodedImage;

    private DocumentDetails() {
    }

    public DocumentDetails(Builder builder) {
        this.docName = builder.docName;
        this.docId = builder.docId;
        this.docEncodedImage = builder.docEncodedImage;
    }

    public String getDocName() {
        return docName;
    }

    public String getDocId() {
        return docId;
    }

    public String getDocEncodedImage() {
        return docEncodedImage;
    }

    public static class Builder {
        private String docName;
        private String docId;
        private String docEncodedImage;

        @Override
        public String toString() {
            return "Builder{" +
                    "docName='" + docName + '\'' +
                    ", docId='" + docId + '\'' +
                    ", docEncodedImage='" + docEncodedImage + '\'' +
                    '}';
        }

        private Builder() {
        }

        public static DocumentDetails.Builder newInstance() {
            return new DocumentDetails.Builder();
        }

        public DocumentDetails build() {
            return new DocumentDetails(this);
        }

        // SETTERS
        public Builder setDocName(String docName) {
            this.docName = docName;
            return this;
        }

        public Builder setDocId(String docId) {
            this.docId = docId;
            return this;
        }

        public Builder setDocEncodedImage(String docEncodedImage) {
            this.docEncodedImage = docEncodedImage;
            return this;
        }
    }

}
