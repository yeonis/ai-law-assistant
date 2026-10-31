import type { LawArticle, LawArticleCompare } from '../types/law';

const API_BASE_URL = 'http://localhost:8080';

export interface LawSearchResponse {
    content: LawArticle[];
    totalElements: number;
    totalPages: number;
    number: number;
}

//법령 조문 검색 api
export async function searchLawArticles(
    keyword: string,
    date: string,
    page: number,
    size: number = 10,
    signal?: AbortSignal
): Promise<LawSearchResponse> {

    const params = new URLSearchParams({
        keyword,
        date,
        page: String(page),
        size: String(size)
    });

    const response = await fetch(
        `${API_BASE_URL}/api/laws/articles/search?${params.toString()}`, { signal }
    );

    if(!response.ok) {
        throw new Error('검색요청 실패');
    }

    return response.json();
}

//이전 법령 버전과 조문 비교 api
export async function compareLawArticles(
    currentMst: number,
    articleNo: number,
    branchNo: number,
    signal?: AbortSignal
): Promise<LawArticleCompare> {

    const params = new URLSearchParams({
        currentMst: String(currentMst),
        articleNo: String(articleNo),
        branchNo: String(branchNo)
    });

    const response = await fetch(
        `${API_BASE_URL}/api/laws/articles/compare/previous?${params.toString()}`,{ signal }
    );

    if(!response.ok) {
        if(response.status === 404) {
            throw new Error('이전 버전 또는 해당 조문을 찾을 수 없습니다.');
        }

        throw new Error('법령 비교 요청에 실패했습니다.');
    }

    return response.json();
}