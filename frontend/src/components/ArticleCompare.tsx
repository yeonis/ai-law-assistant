import { diffWordsWithSpace } from 'diff';
import type { LawArticleCompare } from '../types/law';
import './ArticleCompare.css';

interface Props {
    compareResult: LawArticleCompare;
}

function ArticleCompare({ compareResult }: Props) {
    const { oldArticle, newArticle } = compareResult;

    const changes = diffWordsWithSpace(
        oldArticle.articleContent,
        newArticle.articleContent
    );

    const hasChanges =
        oldArticle.articleTitle !== newArticle.articleTitle ||
        oldArticle.articleContent !== newArticle.articleContent;

    if (!hasChanges) {
        return (
            <div className="compare-notice">
                <span className="compare-notice-icon">✓</span>

                <h3>해당 조문의 변경사항이 없습니다.</h3>

                <p>두 법령 버전의 내용이 동일합니다.</p>

                <span className="compare-notice-version">
                MST {oldArticle.lawSerialNo}
                    {' → '}
                    MST {newArticle.lawSerialNo}
            </span>
            </div>
        );
    }

    return (
        <div className="article-compare">
            <h3>제{oldArticle.articleNo}조 개정 전후 비교</h3>

            <div className="compare-columns">
                <div className="compare-panel">
                    <h4>개정 전</h4>
                    <p>MST {oldArticle.lawSerialNo}</p>

                    <div className="compare-content">
                        {changes.map((part, index) =>
                            part.added ? null : (
                                <span
                                    key={index}
                                    className={
                                        part.removed ? 'diff-removed' : ''
                                    }
                                >
                                    {part.value}
                                </span>
                            )
                        )}
                    </div>
                </div>

                <div className="compare-panel">
                    <h4>개정 후</h4>
                    <p>MST {newArticle.lawSerialNo}</p>

                    <div className="compare-content">
                        {changes.map((part, index) =>
                            part.removed ? null : (
                                <span
                                    key={index}
                                    className={
                                        part.added ? 'diff-added' : ''
                                    }
                                >
                                    {part.value}
                                </span>
                            )
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
}

export default ArticleCompare;