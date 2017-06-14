package uk.ac.fife.twigritte.conversion;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PdfToJpgConverter implements FileConverter {

    public static final String PDF_EXTENSION = ".pdf";
    public static final Log LOG = LogFactory.getLog(PdfToJpgConverter.class);

    @Override
    public File convert(File file) throws IOException {
        String originalFileName = file.getName();
        String baseFileName = originalFileName.substring(0, originalFileName.length() - PDF_EXTENSION.length());

        try (PDDocument document = PDDocument.load(file)) {
            PDFRenderer renderer = new PDFRenderer(document);
            PDPageTree pageTree = document.getDocumentCatalog().getPages();
            int pageCount = pageTree.getCount();

            if (pageCount > 0) {
                if (pageCount > 1) {
                    LOG.warn("Pdf file contains multiple pages, however only first page will be converted.");
                }

                int page = 0; // just convert the first page
                LOG.info("Rendering PDF as image (JPG), this may take awhile...");
                BufferedImage bufferedImage = renderer.renderImageWithDPI(page, 300);
                File outputFile = new File(file.getParentFile(), baseFileName + "-converted-" + page + ".jpg");
                LOG.debug("Converted file: " + outputFile.toString());
                ImageIO.write(bufferedImage, "jpg", outputFile);
                return outputFile;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return "PdfToJpgConverter{}";
    }
}
