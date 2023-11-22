package com.sparta.myselectshop.service;

import com.sparta.myselectshop.dto.ProductMypriceRequestDto;
import com.sparta.myselectshop.dto.ProductRequestDto;
import com.sparta.myselectshop.dto.ProductResponseDto;
import com.sparta.myselectshop.entity.Product;
import com.sparta.myselectshop.entity.User;
import com.sparta.myselectshop.entity.UserRoleEnum;
import com.sparta.myselectshop.naver.dto.ItemDto;
import com.sparta.myselectshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    // myprice 가 100원 이상이여야 하기 때문에 만들어 줘야함
    public static final int MIN_MY_PRICE = 100;

    // 관심 상품 등록
    public ProductResponseDto createProduct(ProductRequestDto requestDto, User user) {
        Product product = productRepository.save(new Product(requestDto, user));
        return new ProductResponseDto(product);
    }

    @Transactional // 변경 감지해서 수정하기 때문에
    // myprice 가 100원 이상이여야 하기 때문에 위에 만듬
    public ProductResponseDto updateProduct(Long id, ProductMypriceRequestDto requestDto) {
        int myprice = requestDto.getMyprice();
        if (myprice < MIN_MY_PRICE) {
            throw new IllegalArgumentException("유효하지 않은 관심 가격입니다. 최소 " + MIN_MY_PRICE + "원 이상으로 설정해 주세요.");
        }

        Product product = productRepository.findById(id).orElseThrow(() ->
                new NullPointerException("해당 상품을 찾을 수 없습니다.")
        );

        product.update(requestDto);

        return new ProductResponseDto(product);
    }

    // 관심상품 조회하기
    public Page<ProductResponseDto> getProducts(User user, int page, int size, String sortBy, boolean isAsc) {
        // 클라이언트에서 반환되는 값이 참이라면 ASC 오름차순, DESC 거짓이면 내림차순
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        UserRoleEnum userRoleEnum = user.getRole();

        // Page 타입으로 한번 감싸져서 넘어 오게 된다
        Page<Product> productList;

        // 정렬
        if (userRoleEnum == UserRoleEnum.USER) {
            productList = productRepository.findAllByUser(user, pageable);
        } else {
            productList = productRepository.findAll(pageable);
        }

        return productList.map(ProductResponseDto::new) ;
    }

    @Transactional
    public void updateBySearch(Long id, ItemDto itemDto) {
        Product product = productRepository.findById(id).orElseThrow(() ->
                new NullPointerException("해당 상품은 존재하지 않습니다.")
        );
        product.updateByItemDto(itemDto);
    }

//    public List<ProductResponseDto> getAllProducts() {
//        List<Product> productList = productRepository.findAll();
//        List<ProductResponseDto> responseDtoList = new ArrayList<>();
//
//        for (Product product : productList) {
//            responseDtoList.add(new ProductResponseDto(product));
//        }
//
//        return responseDtoList;
//    }
}
