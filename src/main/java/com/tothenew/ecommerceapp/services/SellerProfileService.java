package com.tothenew.ecommerceapp.services;

import com.tothenew.ecommerceapp.dtos.SellerAddressDTO;
import com.tothenew.ecommerceapp.dtos.SellerProfileDTO;
import com.tothenew.ecommerceapp.entities.users.Address;
import com.tothenew.ecommerceapp.entities.users.Seller;
import com.tothenew.ecommerceapp.exceptions.ContactInvalidException;
import com.tothenew.ecommerceapp.exceptions.InvalidGstException;
import com.tothenew.ecommerceapp.exceptions.InvalidPasswordException;
import com.tothenew.ecommerceapp.exceptions.ResourceNotFoundException;
import com.tothenew.ecommerceapp.repositories.AddressRepo;
import com.tothenew.ecommerceapp.repositories.SellerRepo;
import com.tothenew.ecommerceapp.utils.SendEmail;
import com.tothenew.ecommerceapp.utils.UserEmailFromToken;
import com.tothenew.ecommerceapp.utils.ValidGst;
import com.tothenew.ecommerceapp.utils.ValidPassword;
import org.apache.tomcat.util.codec.binary.Base64;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Decoder;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class SellerProfileService {

    @Autowired
    UserEmailFromToken userEmailFromToken;

    @Autowired
    SellerRepo sellerRepo;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    SendEmail sendEmail;

    @Autowired
    AddressRepo addressRepo;

    @Autowired
    ValidGst validGst;

    public SellerProfileDTO viewProfile(HttpServletRequest request) throws IOException {
        String sellerEmail = userEmailFromToken.getUserEmail(request);
        Seller seller = sellerRepo.findByEmail(sellerEmail);
        SellerProfileDTO sellerProfileDTO = modelMapper.map(seller,SellerProfileDTO.class);
        // check image format then set
        File f = new File("/home/shiva/Documents/javaPrograms/ecommerce-app/src/main/resources/static/users");
        File[] matchingFiles = new File[2];
        try {
            matchingFiles = f.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.startsWith(sellerProfileDTO.getId().toString());
                }
            });
        }
        catch (Exception ex) {}
        if (matchingFiles.length>0) {
            File file = new File(matchingFiles[0].toString());
            System.out.println(file);
            byte[] fileContent = Files.readAllBytes(file.toPath());
            String encodedFile = new String(Base64.encodeBase64(fileContent), "UTF-8");
            sellerProfileDTO.setImage(encodedFile);
        }

        Set<Address> addresses = seller.getAddresses();
        SellerAddressDTO sellerAddressDTO = modelMapper.map(addresses.stream().findFirst().get(),SellerAddressDTO.class);
        sellerProfileDTO.setAddress(sellerAddressDTO);
        return sellerProfileDTO;
    }

    public String updateSeller(SellerProfileDTO sellerProfileDTO,HttpServletRequest request) {
        Set<String> imageExtensionsAllowed = new HashSet<>();
        imageExtensionsAllowed.add("jpg");
        imageExtensionsAllowed.add("jpeg");
        imageExtensionsAllowed.add("png");
        imageExtensionsAllowed.add("bmp");
        if ((sellerProfileDTO.getCompanyContact() != null) && sellerProfileDTO.getCompanyContact().length()!=10) {
            throw new ContactInvalidException("invalid contact");
        }
        if ((sellerProfileDTO.getGst() != null) && (validGst.checkGstValid(sellerProfileDTO.getGst())!=true)) {
            throw new InvalidGstException("gst format is invalid");
        }
        if (sellerProfileDTO.getGst()!=null) {
            List<Seller> anotherLocalSeller1 = sellerRepo.findByGst(sellerProfileDTO.getGst());
            boolean flag = false;
            for (Seller seller1 : anotherLocalSeller1) {
                if (seller1.getGst().equals(sellerProfileDTO.getGst())) {
                    flag = true;
                    break;
                }
            }
            try {
                if (flag == true) {
                    return "gst should be unique";
                }
            } catch (NullPointerException ex) {
            }
        }
        Seller seller = sellerRepo.findByEmail(userEmailFromToken.getUserEmail(request));
        try {
            if (sellerProfileDTO.getFirstName() != null) {
                seller.setFirstName(sellerProfileDTO.getFirstName());
            }
            if (sellerProfileDTO.getLastName() != null) {
                seller.setLastName(sellerProfileDTO.getLastName());
            }
            if (sellerProfileDTO.getCompanyContact() != null) {
                if (sellerProfileDTO.getCompanyContact().length() != 10) {
                    return "contact invalid";
                }

                seller.setCompanyContact(sellerProfileDTO.getCompanyContact());
            }
            if (sellerProfileDTO.getCompanyName() != null) {
                Seller anotherLocalSeller = sellerRepo.findByCompanyName(sellerProfileDTO.getCompanyName());
                try {
                    if (anotherLocalSeller.getCompanyName().equalsIgnoreCase(seller.getCompanyName())) {
                        return "company name should be unique";
                    }
                } catch (NullPointerException ex) {
                }
                seller.setCompanyName(sellerProfileDTO.getCompanyName());
            }
            if (sellerProfileDTO.getGst() != null) {
                seller.setGst(sellerProfileDTO.getGst());
            }
            if (sellerProfileDTO.getImage() != null) {
                File[] matchingFiles = new File[2];
                File fi;
                try
                {
                    try {
                        File f = new File("/home/shiva/Documents/javaPrograms/ecommerce-app/src/main/resources/static/users");
                            matchingFiles = f.listFiles(new FilenameFilter() {
                            public boolean accept(File dir, String name) {
                                return name.startsWith(seller.getId().toString());
                            }
                        });
                        fi = new File(matchingFiles[0].toString());
                        Path fileToDeletePath = Paths.get(String.valueOf(fi));
                        Files.delete(fileToDeletePath);
                    } catch(Exception ex) {}

                    String parts[] = sellerProfileDTO.getImage().split(",");
                    String imageName = parts[0];
                    String fileExtensionArr[] = imageName.split("/");
                    String fileExtension[] = fileExtensionArr[1].split(";");
                    if (!imageExtensionsAllowed.contains(fileExtension[0])) {
                        return fileExtension + " image format not allowed";
                    }
                    String imageString = parts[1];

                    BufferedImage image = null;
                    byte[] imageByte;

                    BASE64Decoder decoder = new BASE64Decoder();
                    imageByte = decoder.decodeBuffer(imageString);
                    ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
                    image = ImageIO.read(bis);
                    bis.close();
                    String path = "/home/shiva/Documents/javaPrograms/ecommerce-app/src/main/resources/static/users/" + seller.getId();

                    File outputFile = new File(path+"."+fileExtension[0]);
                    ImageIO.write(image, fileExtension[0], outputFile);
                }
                catch(Exception e)
                {
                    return "error = "+e;
                }
            }
        } catch (NullPointerException ex) {}

        sellerRepo.save(seller);
        return "Success";
    }

    public String updatePassword(String pass,String cPass,HttpServletRequest request) {
        if (!pass.contentEquals(cPass)) {
            return "Password and confirm password does not match";
        }
        if (!ValidPassword.isValidPassword(pass)) {
            throw new InvalidPasswordException("password format invalid");
        }
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        Seller seller = sellerRepo.findByEmail(userEmailFromToken.getUserEmail(request));
        seller.setPassword(passwordEncoder.encode(pass));

        sellerRepo.save(seller);

        sendEmail.sendEmail("PASSWORD CHANGED","Your password changed",seller.getEmail());

        return "Success";
    }

    public String updateAddress(java.lang.Long id, SellerAddressDTO addressDTO, HttpServletRequest request) {
        Optional<Address> address = addressRepo.findById(id);
        if (!address.isPresent()) {
            throw  new ResourceNotFoundException("no address fount with id " + id);
        }
        Seller seller = sellerRepo.findByEmail(userEmailFromToken.getUserEmail(request));
        Set<Address> addresses = seller.getAddresses();
        addresses.forEach(a->{
            if (a.getId() == address.get().getId()) {
                a.setAddress(addressDTO.getAddress());
                a.setCity(addressDTO.getCity());
                a.setCountry(addressDTO.getCountry());
                a.setState(addressDTO.getState());
                a.setZipCode(addressDTO.getZipCode());
                a.setAddress(addressDTO.getAddress());
            }
        });
        sellerRepo.save(seller);
        return "Success";
    }
}
