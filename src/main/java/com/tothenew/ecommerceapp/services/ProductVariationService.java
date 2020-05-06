package com.tothenew.ecommerceapp.services;

import com.tothenew.ecommerceapp.dtos.ProductVariationDTO;
import com.tothenew.ecommerceapp.entities.product.Product;
import com.tothenew.ecommerceapp.entities.product.ProductVariation;
import com.tothenew.ecommerceapp.entities.users.Seller;
import com.tothenew.ecommerceapp.exceptions.ResourceNotFoundException;
import com.tothenew.ecommerceapp.repositories.CategoryMetadataFieldValuesRepo;
import com.tothenew.ecommerceapp.repositories.ProductRepo;
import com.tothenew.ecommerceapp.repositories.ProductVariationRepo;
import com.tothenew.ecommerceapp.repositories.SellerRepo;
import com.tothenew.ecommerceapp.utils.UserEmailFromToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Decoder;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class ProductVariationService {

    @Autowired
    ProductRepo productRepo;
    @Autowired
    CategoryMetadataFieldValuesRepo valuesRepo;
    @Autowired
    ProductVariationRepo variationRepo;
    @Autowired
    UserEmailFromToken userEmailFromToken;
    @Autowired
    SellerRepo sellerRepo;

    public String add(ProductVariationDTO productVariationDTO) {
        Optional<Product> product = productRepo.findById(productVariationDTO.getProductId());
        if (!product.isPresent()) {
            throw new ResourceNotFoundException(productVariationDTO.getProductId() + " not exist");
        }
        try {
            if (productVariationDTO.getQuantityAvailable() != null) {
                if (productVariationDTO.getQuantityAvailable() <= 0) {
                    return "quantity should be 0 or more";
                }
            }
            if (productVariationDTO.getPrice() != null) {
                if (productVariationDTO.getPrice() <= 0) {
                    return "price should be 0 or more";
                }
            }
            if (!product.get().getActive()) {
                return "product is not active";
            }
            if (product.get().getDeleted()) {
                return "product is deleted";
            }
        } catch (Exception ex) {
        }

        List<Object[]> categoryFieldValues = valuesRepo.findCategoryMetadataFieldValuesById(product.get().getCategory().getId());
        HashMap fieldValueMap = new HashMap<>();
        categoryFieldValues.forEach(c -> {
            List<Object> arr = Arrays.asList(c);
            for (int i = 0; i < arr.size(); i++) {
                fieldValueMap.put(arr.get(0), arr.get(i));
            }
        });
        Set<String> fieldValueDb = new HashSet<>();
        Set<String> fieldValueReq = new HashSet<>();
        fieldValueMap.forEach((k, v) -> {
            fieldValueDb.add(k.toString().trim().replaceAll("\\s", ""));
            String[] arr = v.toString().split(",");
            for (String s : arr) {
                fieldValueDb.add(s.trim().replaceAll("\\s", ""));
            }

        });
        productVariationDTO.getFiledIdValues().forEach((k, v) -> {
            fieldValueReq.add(k.trim().replaceAll("\\s", ""));
            v.forEach(s -> {
                fieldValueReq.add(s.trim().replaceAll("\\s", ""));
            });
        });
        fieldValueReq.forEach(s -> {
            if (!fieldValueDb.contains(s)) {
                throw new RuntimeException("invalid field or value");
            }
        });

        Set<String> imageExtensionsAllowed = new HashSet<>();
        imageExtensionsAllowed.add("jpg");
        imageExtensionsAllowed.add("jpeg");
        imageExtensionsAllowed.add("png");
        imageExtensionsAllowed.add("bmp");
        String path;
        if (!(productVariationDTO.getPrimaryImage() == null)) {
            try {
                String parts[] = productVariationDTO.getPrimaryImage().split(",");
                String imageName = parts[0];
                String fileExtensionArr[] = imageName.split("/");
                String fileExtension[] = fileExtensionArr[1].split(";");
                if (!imageExtensionsAllowed.contains(fileExtension[0])) {
                    return fileExtension[0] + " image format not allowed";
                }
                String imageString = parts[1];

                BufferedImage image = null;
                byte[] imageByte;

                BASE64Decoder decoder = new BASE64Decoder();
                imageByte = decoder.decodeBuffer(imageString);
                ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
                image = ImageIO.read(bis);
                bis.close();
                path = "/home/shiva/Documents/javaPrograms/ecommerce-app/src/main/resources/static/products/" + product.get().getId() + "/variations/" + variationRepo.getNextValMySequence() + "PI";

                File outputFile = new File(path + "." + fileExtension[0]);
                ImageIO.write(image, fileExtension[0], outputFile);
            } catch (Exception e) {
                return "error = " + e;
            }
        }
        if (!productVariationDTO.getSecondaryImages().isEmpty()) {
            System.out.println(productVariationDTO.getSecondaryImages().size());
            for (int i = 0; i < productVariationDTO.getSecondaryImages().size(); i++) {
                try {
                    String parts[] = productVariationDTO.getSecondaryImages().get(i).split(",");
                    String imageName = parts[0];
                    String fileExtensionArr[] = imageName.split("/");
                    String fileExtension[] = fileExtensionArr[1].split(";");
                    System.out.println(imageExtensionsAllowed.contains(fileExtension[0]));
                    if (!imageExtensionsAllowed.contains(fileExtension[0])) {
                        return fileExtension[0] + " img format not allowed";
                    }
                    String imageString = parts[1];

                    BufferedImage img = null;
                    byte[] imageByte;

                    BASE64Decoder decoder = new BASE64Decoder();
                    imageByte = decoder.decodeBuffer(imageString);
                    ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
                    img = ImageIO.read(bis);
                    bis.close();
                    String pathS = "/home/shiva/Documents/javaPrograms/ecommerce-app/src/main/resources/static/products/" + product.get().getId() + "/variations/" + variationRepo.getNextValMySequence() + "SI" + i;
                    System.out.println(pathS + "---------------");
                    File outputFile = new File(pathS + "." + fileExtension[0]);
                    ImageIO.write(img, fileExtension[0], outputFile);
                } catch (Exception e) {
                }

            }
        }
        ProductVariation productVariation = new ProductVariation();
        productVariation.setMetadata(productVariationDTO.getFiledIdValues());
        productVariation.setPrimaryImageName("P1" + productVariationDTO.getProductId());
        productVariation.setActive(true);
        productVariation.setProduct(product.get());
        product.get().getProductVariations().add(productVariation);
        if (productVariationDTO.getPrice() != null) {
            productVariation.setPrice(productVariationDTO.getPrice());
        }
        if (productVariationDTO.getQuantityAvailable() != null) {
            productVariation.setQuantityAvailable(productVariationDTO.getQuantityAvailable().longValue());
        }
        variationRepo.save(productVariation);
        productRepo.save(product.get());
        return "Success";
    }

    public ProductVariation viewProduct(Long id, HttpServletRequest request) {
        String userEmail = userEmailFromToken.getUserEmail(request);
        Seller seller = sellerRepo.findByEmail(userEmail);
        Optional<ProductVariation> productVariation = variationRepo.findById(id);
        if (!productVariation.isPresent()) {
            throw new ResourceNotFoundException(id + " product variation not found");
        }
        if (productVariation.get().getProduct().getSeller().getId() != seller.getId()) {
            throw new ResourceNotFoundException("invalid seller");
        }
        try {
            if (productVariation.get().getProduct().getDeleted()) {
                throw new ResourceNotFoundException(id + " product is deleted");
            }
        } catch (Exception ex) {
        }
        return productVariation.get();
    }

    public List<?> viewAll(Long productId, HttpServletRequest request, String page, String size, String sortBy, String order, Optional<String> query) {
        Optional<Product> productCheck = productRepo.findById(productId);
        if (!productCheck.isPresent()) {
            throw new ResourceNotFoundException(productId + " product not found");
        }
        if (query.isPresent()) {
            List<ProductVariation> productVariations = new ArrayList<>();
            ProductVariation productVariation = viewProduct(Long.parseLong(query.get()), request);
            productVariations.add(productVariation);
            return productVariations;
        }
        String userEmail = userEmailFromToken.getUserEmail(request);
        Seller seller = sellerRepo.findByEmail(userEmail);
        Optional<Product> product = productRepo.findById(productId);
        if (product.get().getSeller().getId() != seller.getId()) {
            throw new ResourceNotFoundException("invalid seller");
        }
        try {
            if (product.get().getDeleted()) {
                throw new ResourceNotFoundException("deleted product");
            }
        } catch (Exception ex) {
        }
        List<ProductVariation> productVariations = variationRepo.getAll(productId, PageRequest.of(Integer.parseInt(page), Integer.parseInt(size), Sort.by(Sort.Direction.fromString(order), sortBy)));
        return productVariations;
    }


    public String updateProductVariation(HttpServletRequest request, ProductVariationDTO productVariationDTO, Optional<Boolean> isActive) {
        Optional<ProductVariation> productVariation = variationRepo.findById(productVariationDTO.getProductId());
        if (!productVariation.isPresent()) {
            throw new ResourceNotFoundException(productVariationDTO.getProductId() + " not found");
        }
        String sellerEmail = userEmailFromToken.getUserEmail(request);
        Seller seller = sellerRepo.findByEmail(sellerEmail);
        if (productVariation.get().getProduct().getSeller().getId() != seller.getId()) {
            throw new ResourceNotFoundException("invalid seller");
        }
        try {
            if (productVariation.get().getProduct().getDeleted()) {
                throw new ResourceNotFoundException(productVariationDTO.getProductId() + " product is deleted");
            }
        } catch (Exception ex) {
        }
        if (productVariation.get().getProduct().getDeleted()) {
            throw new ResourceNotFoundException("deleted product");
        }
        if (productVariationDTO.getFiledIdValues() != null) {
            List<Object[]> categoryFieldValues = valuesRepo.findCategoryMetadataFieldValuesById(productVariation.get().getProduct().getCategory().getId());
            HashMap fieldValueMap = new HashMap<>();
            categoryFieldValues.forEach(c -> {
                List<Object> arr = Arrays.asList(c);
                for (int i = 0; i < arr.size(); i++) {
                    fieldValueMap.put(arr.get(0), arr.get(i));
                }
            });
            Set<String> fieldValueDb = new HashSet<>();
            Set<String> fieldValueReq = new HashSet<>();
            fieldValueMap.forEach((k, v) -> {
                fieldValueDb.add(k.toString().trim().replaceAll("\\s", ""));
                String[] arr = v.toString().split(",");
                for (String s : arr) {
                    fieldValueDb.add(s.trim().replaceAll("\\s", ""));
                }

            });
            productVariationDTO.getFiledIdValues().forEach((k, v) -> {
                fieldValueReq.add(k.trim().replaceAll("\\s", ""));
                v.forEach(s -> {
                    fieldValueReq.add(s.trim().replaceAll("\\s", ""));
                });
            });
            fieldValueReq.forEach(s -> {
                if (!fieldValueDb.contains(s)) {
                    throw new RuntimeException("invalid field or value");
                }
            });
            productVariation.get().setMetadata(productVariationDTO.getFiledIdValues());
        }
        if (productVariationDTO.getPrice() != null) {
            productVariation.get().setPrice(productVariationDTO.getPrice());
        }
        if (productVariationDTO.getQuantityAvailable() != null) {
            productVariation.get().setQuantityAvailable(productVariationDTO.getQuantityAvailable().longValue());
        }
        if (isActive.isPresent()) {
            productVariation.get().setActive(isActive.get());
        }
        Set<String> imageExtensionsAllowed = new HashSet<>();
        imageExtensionsAllowed.add("jpg");
        imageExtensionsAllowed.add("jpeg");
        imageExtensionsAllowed.add("png");
        imageExtensionsAllowed.add("bmp");
        String path;
        if (!(productVariationDTO.getPrimaryImage() == null)) {
            File fi;
            File[] matchingFiles = new File[5];
            try {

                try {
                    File f = new File("/home/shiva/Documents/javaPrograms/ecommerce-app/src/main/resources/static/products/" + productVariation.get().getProduct().getId() + "/variations");
                    matchingFiles = f.listFiles(new FilenameFilter() {
                        public boolean accept(File dir, String name) {
                            return name.startsWith(productVariationDTO.getProductId().toString() + "PI");
                        }
                    });
                    fi = new File(matchingFiles[0].toString());
                    Path fileToDeletePath = Paths.get(String.valueOf(fi));
                    Files.delete(fileToDeletePath);
                } catch (Exception ex) {
                }


                String parts[] = productVariationDTO.getPrimaryImage().split(",");
                String imageName = parts[0];
                String fileExtensionArr[] = imageName.split("/");
                String fileExtension[] = fileExtensionArr[1].split(";");
                if (!imageExtensionsAllowed.contains(fileExtension[0])) {
                    return fileExtension[0] + " image format not allowed";
                }
                String imageString = parts[1];

                BufferedImage image = null;
                byte[] imageByte;

                BASE64Decoder decoder = new BASE64Decoder();
                imageByte = decoder.decodeBuffer(imageString);
                ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
                image = ImageIO.read(bis);
                bis.close();
                path = "/home/shiva/Documents/javaPrograms/ecommerce-app/src/main/resources/static/products/" + productVariation.get().getProduct().getId() + "/variations/" + productVariationDTO.getProductId() + "PI";

                File outputFile = new File(path + "." + fileExtension[0]);
                ImageIO.write(image, fileExtension[0], outputFile);
            } catch (Exception e) {
                return "error = " + e;
            }
        }

        if (productVariationDTO.getSecondaryImages() != null) {
            File fi;
            File[] matchingFiles = new File[5];
            try {

                try {
                    File f = new File("/home/shiva/Documents/javaPrograms/ecommerce-app/src/main/resources/static/products/" + productVariation.get().getProduct().getId() + "/variations");
                    matchingFiles = f.listFiles(new FilenameFilter() {
                        public boolean accept(File dir, String name) {
                            return name.startsWith(productVariationDTO.getProductId().toString() + "SI");
                        }
                    });
                    for (int i = 0; i < matchingFiles.length; i++) {
                        fi = new File(matchingFiles[i].toString());
                        Path fileToDeletePath = Paths.get(String.valueOf(fi));
                        Files.delete(fileToDeletePath);
                    }
                } catch (Exception ex) {
                }

                for (int i = 0; i < productVariationDTO.getSecondaryImages().size(); i++) {
                    try {
                        String parts[] = productVariationDTO.getSecondaryImages().get(i).split(",");
                        String imageName = parts[0];
                        String fileExtensionArr[] = imageName.split("/");
                        String fileExtension[] = fileExtensionArr[1].split(";");
                        System.out.println(imageExtensionsAllowed.contains(fileExtension[0]));
                        if (!imageExtensionsAllowed.contains(fileExtension[0])) {
                            return fileExtension[0] + " img format not allowed";
                        }
                        String imageString = parts[1];

                        BufferedImage img = null;
                        byte[] imageByte;

                        BASE64Decoder decoder = new BASE64Decoder();
                        imageByte = decoder.decodeBuffer(imageString);
                        ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
                        img = ImageIO.read(bis);
                        bis.close();
                        String pathS = "/home/shiva/Documents/javaPrograms/ecommerce-app/src/main/resources/static/products/" + +productVariation.get().getProduct().getId() + "/variations/" + productVariationDTO.getProductId() + "SI" + i;

                        System.out.println(pathS + "---------------");
                        File outputFile = new File(pathS + "." + fileExtension[0]);
                        ImageIO.write(img, fileExtension[0], outputFile);
                    } catch (Exception e) {
                    }

                }

            } catch (Exception ex) {
            }

            variationRepo.save(productVariation.get());
        }


        return "Success";
    }
}
